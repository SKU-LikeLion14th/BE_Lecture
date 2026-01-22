package com.example.demo.controller;

import com.example.demo.DTO.MemberDTO;
import com.example.demo.domain.Member;
import com.example.demo.service.MemberService;
import com.example.demo.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController // 이 클래스가 REST API 컨트롤러임을 명시 (JSON 반환)
@RequiredArgsConstructor // final 필드에 대한 생성자를 자동으로 생성 (의존성 주입)
@RequestMapping("/api") // 이 컨트롤러의 모든 API는 /api 로 시작함
public class MemberController {

    private final MemberService memberService; // 서비스 계층 의존성 주입

    // 1. 회원가입 API
    // HTTP Method: POST
    // URL: /api/members
    // Body: CreateMemberRequest (JSON)
    @PostMapping("/members")
    public MemberDTO.Result<Long> saveMember(@RequestBody MemberDTO.Request.Create request) {
        // DTO(Data Transfer Object)에서 도메인 객체로 변환
        Member member = new Member();
        member.setUsername(request.getUsername());
        member.setPassword(request.getPassword());
        member.setName(request.getName());

        // 서비스 계층 호출하여 회원가입 처리
        Long id = memberService.join(member);

        // 결과 반환 (Result 래퍼 클래스 사용)
        return new MemberDTO.Result<>(id);
    }

    // 2. 전체 회원 조회 API
    // HTTP Method: GET
    // URL: /api/members
    @GetMapping("/members")
    public MemberDTO.Result<List<MemberDTO.Response.Member>> findAllMembers() {
        // 모든 회원 조회
        List<Member> findMembers = memberService.findMembers();

        // 도메인 객체(Member)를 응답용 DTO(MemberResponse)로 변환
        // 이유: 도메인 객체를 직접 반환하면 불필요한 정보(비밀번호 등)가 노출되거나 API 스펙이 변경될 수 있음
        List<MemberDTO.Response.Member> collect = findMembers.stream()
                .map(m -> new MemberDTO.Response.Member(m.getId(), m.getUsername(), m.getName()))
                .collect(Collectors.toList());

        return new MemberDTO.Result<>(collect);
    }

    // 2-1. 단일 회원 조회 API
    // HTTP Method: GET
    // URL: /api/members/{id}
    @GetMapping("/members/{id}")
    public MemberDTO.Result<?> findMemberById(@PathVariable Long id) {
        // 서비스 계층에서 ID로 회원 조회
        Member findMember = memberService.findOne(id);

        // 만약 해당 ID의 회원이 없다면 에러 메시지 반환
        if (findMember == null) {
            return new MemberDTO.Result<>("조회 실패: ID가 " + id + "인 회원을 찾을 수 없습니다.");
        }

        // 조회 성공 시 DTO로 변환하여 반환
        MemberDTO.Response.Member response = new MemberDTO.Response.Member(
                findMember.getId(),
                findMember.getUsername(),
                findMember.getName());

        return new MemberDTO.Result<>(response);
    }

    // 3. 회원 정보 수정 API
    // HTTP Method: PUT
    // URL: /api/members/{id}
    // Header: Authorization: Bearer {JWT_TOKEN} (필수!)
    //
    // ── 🔐 JWT 인증이 필요한 이유 ──
    // 회원 정보 수정은 민감한 작업이므로, 로그인한 사용자만 할 수 있어야 합니다.
    // 따라서 헤더에 유효한 JWT 토큰을 포함해야만 요청이 처리됩니다.
    //
    // ── 📝 사용 방법 ──
    // 1. 먼저 /api/login으로 로그인하여 JWT 토큰을 받습니다.
    // 2. 이후 PUT 요청 시 헤더에 다음과 같이 토큰을 포함합니다:
    // Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
    @PutMapping("/members/{id}")
    public MemberDTO.Result<?> updateMember(
            @PathVariable Long id, // URL 경로에 있는 id 값을 가져옴
            @RequestBody MemberDTO.Request.Update request, // Body에 있는 JSON 데이터를 객체로 매핑
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader) { // 헤더에서
                                                                                                    // Authorization 값
                                                                                                    // 가져오기

        // ── 1단계: JWT 토큰 추출 및 검증 ──
        String token = extractTokenFromHeader(authorizationHeader);
        if (token == null) {
            // 토큰이 없거나 형식이 잘못된 경우
            return new MemberDTO.Result<>("인증 실패: JWT 토큰이 필요합니다. 헤더에 'Authorization: Bearer {토큰}' 형식으로 전달해주세요.");
        }

        // ── 2단계: 토큰 유효성 검증 ──
        if (!JwtUtil.validateToken(token)) {
            // 토큰이 유효하지 않은 경우 (만료됨, 위변조됨 등)
            return new MemberDTO.Result<>("인증 실패: 유효하지 않은 토큰입니다. 다시 로그인해주세요.");
        }

        // ── 3단계: 토큰에서 회원 ID 추출 및 권한 확인 (선택사항) ──
        // 보안 강화: 토큰에 있는 회원 ID와 수정하려는 회원 ID가 일치하는지 확인
        // (자신의 정보만 수정할 수 있도록)
        try {
            Long tokenMemberId = JwtUtil.getMemberIdFromToken(token);

            // 💡 디버깅을 위한 출력 (초보자용): 두 ID가 어떻게 비교되는지 콘솔에서 확인할 수 있습니다.
            System.out.println("[Step 3 Debug] Token ID: " + tokenMemberId + " ("
                    + tokenMemberId.getClass().getSimpleName() + ")");
            System.out.println("[Step 3 Debug] Target ID: " + id + " (" + id.getClass().getSimpleName() + ")");

            if (!tokenMemberId.equals(id)) {
                // 토큰의 회원 ID와 수정하려는 회원 ID가 다르면 → 권한 없음
                return new MemberDTO.Result<>(
                        "권한 없음: 자신의 정보만 수정할 수 있습니다. (Token ID: " + tokenMemberId + ", Target ID: " + id + ")");
            }
        } catch (Exception e) {
            // 토큰에서 ID 추출 실패
            return new MemberDTO.Result<>("인증 실패: 토큰에서 회원 정보를 읽을 수 없습니다. (사유: " + e.getMessage() + ")");
        }

        // ── 4단계: 모든 검증 통과! 서비스 계층 호출하여 수정 처리 ──
        memberService.update(id, request.getName(), request.getPassword());

        // ── 5단계: 수정된 정보 조회하여 반환 ──
        Member findMember = memberService.findOne(id);
        return new MemberDTO.Result<>(
                new MemberDTO.Response.Member(findMember.getId(), findMember.getUsername(), findMember.getName()));
    }

    // 4. 회원 삭제 API
    // HTTP Method: DELETE
    // URL: /api/members/{id}
    // Header: Authorization: Bearer {JWT_TOKEN} (필수!)
    //
    // ── 🔐 JWT 인증이 필요한 이유 ──
    // 회원 삭제는 매우 민감한 작업이므로, 로그인한 사용자만 할 수 있어야 합니다.
    // 따라서 헤더에 유효한 JWT 토큰을 포함해야만 요청이 처리됩니다.
    //
    // ── 📝 사용 방법 ──
    // 1. 먼저 /api/login으로 로그인하여 JWT 토큰을 받습니다.
    // 2. 이후 DELETE 요청 시 헤더에 다음과 같이 토큰을 포함합니다:
    // Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
    @DeleteMapping("/members/{id}")
    public MemberDTO.Result<String> deleteMember(
            @PathVariable Long id, // URL 경로에 있는 id 값을 가져옴
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader) { // 헤더에서
                                                                                                    // Authorization 값
                                                                                                    // 가져오기

        // ── 1단계: JWT 토큰 추출 및 검증 ──
        String token = extractTokenFromHeader(authorizationHeader);
        if (token == null) {
            // 토큰이 없거나 형식이 잘못된 경우
            return new MemberDTO.Result<>("인증 실패: JWT 토큰이 필요합니다. 헤더에 'Authorization: Bearer {토큰}' 형식으로 전달해주세요.");
        }

        // ── 2단계: 토큰 유효성 검증 ──
        if (!JwtUtil.validateToken(token)) {
            // 토큰이 유효하지 않은 경우 (만료됨, 위변조됨 등)
            return new MemberDTO.Result<>("인증 실패: 유효하지 않은 토큰입니다. 다시 로그인해주세요.");
        }

        // ── 3단계: 토큰에서 회원 ID 추출 및 권한 확인 (선택사항) ──
        // 보안 강화: 토큰에 있는 회원 ID와 삭제하려는 회원 ID가 일치하는지 확인
        // (자신의 계정만 삭제할 수 있도록)
        try {
            Long tokenMemberId = JwtUtil.getMemberIdFromToken(token);

            // 💡 디버깅을 위한 출력 (초보자용): 두 ID가 어떻게 비교되는지 콘솔에서 확인할 수 있습니다.
            System.out.println("[Step 3 Debug] Token ID: " + tokenMemberId + " ("
                    + tokenMemberId.getClass().getSimpleName() + ")");
            System.out.println("[Step 3 Debug] Target ID: " + id + " (" + id.getClass().getSimpleName() + ")");

            if (!tokenMemberId.equals(id)) {
                // 토큰의 회원 ID와 삭제하려는 회원 ID가 다르면 → 권한 없음
                return new MemberDTO.Result<>(
                        "권한 없음: 자신의 계정만 삭제할 수 있습니다. (Token ID: " + tokenMemberId + ", Target ID: " + id + ")");
            }
        } catch (Exception e) {
            // 토큰에서 ID 추출 실패
            return new MemberDTO.Result<>("인증 실패: 토큰에서 회원 정보를 읽을 수 없습니다. (사유: " + e.getMessage() + ")");
        }

        // ── 4단계: 모든 검증 통과! 서비스 계층 호출하여 삭제 처리 ──
        memberService.delete(id);
        return new MemberDTO.Result<>("회원 삭제 완료");
    }

    // 5. 로그인 API (JWT 발급 시연)
    // HTTP Method: POST
    // URL: /api/login
    @PostMapping("/login")
    public MemberDTO.Result<String> login(@RequestBody MemberDTO.Request.Login request) {
        // 아이디/비밀번호 확인
        Member loginMember = memberService.login(request.getUsername(), request.getPassword());

        if (loginMember == null) {
            return new MemberDTO.Result<>("로그인 실패");
        }

        // 로그인 성공 시 JWT 토큰 생성 및 반환
        String token = JwtUtil.createToken(loginMember.getId(), loginMember.getName());
        if (token == null) {
            return new MemberDTO.Result<>("토큰 생성 실패");
        }
        return new MemberDTO.Result<>(token);
    }

    /**
     * Authorization 헤더에서 JWT 토큰을 추출하는 헬퍼 메서드
     * 
     * ── 💡 사용 시나리오 ──
     * 클라이언트가 보낸 헤더는 다음과 같은 형식입니다:
     * Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
     * 
     * 이 메서드는 "Bearer " 부분을 제거하고 순수한 토큰 문자열만 반환합니다.
     * 
     * ── 🔍 처리 과정 ──
     * 1. 헤더가 null이거나 비어있으면 → null 반환
     * 2. "Bearer "로 시작하지 않으면 → null 반환 (형식 오류)
     * 3. "Bearer "를 제거한 나머지 부분(토큰) 반환
     * 
     * @param authorizationHeader HTTP 헤더의 Authorization 값 (예: "Bearer eyJ...")
     * @return 추출된 JWT 토큰 문자열 (예: "eyJ..."), 추출 실패 시 null
     */
    private String extractTokenFromHeader(String authorizationHeader) {
        // ── 1단계: 헤더 존재 여부 확인 ──
        if (authorizationHeader == null || authorizationHeader.trim().isEmpty()) {
            // 헤더가 없거나 빈 문자열이면 토큰 없음
            return null;
        }

        // ── 2단계: "Bearer " 접두사 확인 및 제거 ──
        // Bearer는 JWT 토큰을 전달할 때 사용하는 표준 형식입니다.
        // 형식: "Bearer {토큰}" (공백 주의!)
        String bearerPrefix = "Bearer ";
        if (!authorizationHeader.startsWith(bearerPrefix)) {
            // "Bearer "로 시작하지 않으면 형식 오류
            return null;
        }

        // ── 3단계: "Bearer "를 제거한 나머지 부분(토큰) 추출 ──
        String token = authorizationHeader.substring(bearerPrefix.length()).trim();

        // 토큰이 비어있지 않으면 반환
        if (token.isEmpty()) {
            return null;
        }

        return token;
    }
}
