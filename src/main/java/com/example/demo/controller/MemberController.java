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

    // 3. 회원 정보 수정 API
    // HTTP Method: PUT
    // URL: /api/members/{id}
    @PutMapping("/members/{id}")
    public MemberDTO.Result<MemberDTO.Response.Member> updateMember(
            @PathVariable Long id, // URL 경로에 있는 id 값을 가져옴
            @RequestBody MemberDTO.Request.Update request) { // Body에 있는 JSON 데이터를 객체로 매핑

        // 서비스 계층 호출하여 수정 처리
        memberService.update(id, request.getName(), request.getPassword());

        // 수정된 정보 조회하여 반환
        Member findMember = memberService.findOne(id);
        return new MemberDTO.Result<>(new MemberDTO.Response.Member(findMember.getId(), findMember.getUsername(), findMember.getName()));
    }

    // 4. 회원 삭제 API
    // HTTP Method: DELETE
    // URL: /api/members/{id}
    @DeleteMapping("/members/{id}")
    public MemberDTO.Result<String> deleteMember(@PathVariable Long id) {
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
}
