package com.example.embabel.controller;

import com.example.embabel.domain.Member;
import com.example.embabel.dto.MemberDTO;
import com.example.embabel.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MemberController {
    private final MemberService memberService;

    //1. 회원가입
    @PostMapping("/members")
    public MemberDTO.Result<Long> saveMember(@RequestBody MemberDTO.Request.Create request) {
        Member member = new Member();
        member.setUserId(request.getUserId());
        member.setPassword(request.getPassword());
        member.setUsername(request.getUsername());

        //서비스 계층 호출하여 회원가입 처리
        Long id = memberService.signUp(member);

        //결과 반환
        return new MemberDTO.Result<>(id); // <>가 비어있는 이유는 타입추론 때문 ㅇㅇ

    }
}
