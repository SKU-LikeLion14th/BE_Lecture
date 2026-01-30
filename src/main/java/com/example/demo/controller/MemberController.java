package com.example.demo.controller;

import com.example.demo.DTO.MemberDTO;
import com.example.demo.domain.Member;
import com.example.demo.service.MemberService;
import com.example.demo.security.JwtUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController // 이 클래스가 REST API 컨트롤러임을 명시 (JSON 반환)
@RequiredArgsConstructor // final 필드에 대한 생성자를 자동으로 생성 (의존성 주입)
@RequestMapping("/api") // 이 컨트롤러의 모든 API는 /api 로 시작함
public class MemberController {

    private final MemberService memberService;
    private final JwtUtility jwtUtility;// 서비스 계층 의존성 주입

    // 1. 회원가입 API
    // HTTP Method: POST
    // URL: /api/members
    // Body: CreateMemberRequest (JSON)
    @PostMapping("/members")
    public MemberDTO.Result<Long> saveMember(@RequestBody MemberDTO.Request.Create request) {
        // DTO(Data Transfer Object)에서 도메인 객체로 변환
        Member member = new Member();
        member.setUserId(request.getUserId());
        member.setPassword(request.getPassword());
        member.setUsername(request.getUsername());

        // 서비스 계층 호출하여 회원가입 처리
        Long id = memberService.signUp(member);

        // 결과 반환 (Result 래퍼 클래스 사용)
        return new MemberDTO.Result<>(id);
    }

    // 2. 전체 회원 조회 API
    // HTTP Method: GET
    // URL: /api/members
    @GetMapping("/members")
    public MemberDTO.Result<List<MemberDTO.Response.Member>> findAllMembers() {
        // 모든 회원 조회
        List<Member> findMembers = memberService.findAll();

        // 도메인 객체(Member)를 응답용 DTO(MemberResponse)로 변환
        // 이유: 도메인 객체를 직접 반환하면 불필요한 정보(비밀번호 등)가 노출되거나 API 스펙이 변경될 수 있음
        List<MemberDTO.Response.Member> collect = findMembers.stream()
                .map(m -> new MemberDTO.Response.Member(m.getId(), m.getUsername(), m.getUsername()))
                .collect(Collectors.toList());

        return new MemberDTO.Result<>(collect);
    }

    // 2-1. 단일 회원 조회 API
    // HTTP Method: GET
    // URL: /api/members/{id}
    @GetMapping("/members/{id}")
    public MemberDTO.Result<?> findMemberById(@PathVariable Long id) {
        // 서비스 계층에서 ID로 회원 조회
        Member findMember = memberService.findById(id);

        // 만약 해당 ID의 회원이 없다면 에러 메시지 반환
        if (findMember == null) {
            return new MemberDTO.Result<>("조회 실패: ID가 " + id + "인 회원을 찾을 수 없습니다.");
        }

        // 조회 성공 시 DTO로 변환하여 반환
        MemberDTO.Response.Member response = new MemberDTO.Response.Member(
                findMember.getId(),
                findMember.getUsername(),
                findMember.getUsername());

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
    @PutMapping("/members")
    public MemberDTO.Result<?> updateMember(
            @RequestBody MemberDTO.Request.Update request, // Body에 있는 JSON 데이터를 객체로 매핑
            @RequestHeader("Authorization") String token) { // 헤더에서


        Long id = memberService.tokenToMember(token).getId();
        memberService.update(id, request.getUsername(), request.getPassword());

        Member findmember = memberService.findById(id);
        return new MemberDTO.Result<>(new MemberDTO.Response.Member(findmember.getId(), findmember.getUserId(), findmember.getUsername()));
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
    @DeleteMapping("/members")
    public MemberDTO.Result<String> deleteMember(@RequestHeader("Authorization") String token) {

        Long id = memberService.tokenToMember(token).getId();
        memberService.delete(id);

        return new MemberDTO.Result<>("회원삭제 완료");
    }

    // 5. 로그인 API (JWT 발급 시연)
    // HTTP Method: POST
    // URL: /api/login
    @PostMapping("/login")
    public MemberDTO.Result<String> login(@RequestBody MemberDTO.Request.Login request) {
        String token = memberService.login(request.getUserId(), request.getPassword());
        return new MemberDTO.Result<>(token);
    }



}
