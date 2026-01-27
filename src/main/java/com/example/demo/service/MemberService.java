package com.example.demo.service;

import com.example.demo.domain.Member;
import com.example.demo.repository.MemberRepository;
import com.example.demo.security.JwtUtility;
import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
// import org.springframework.transaction.annotation.Transactional; // JPA 트랜잭션 제거

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepository;
    private final JwtUtility jwtUtility;

    //토큰을 멤버 객체로 반환
    public Member tokenToMember(String token){
        return memberRepository.findByUserId(jwtUtility.getClaimsFromJwt(token).getSubject());
    }

    // [C] 회원가입
    @Transactional
    public Long signUp(Member member) {
        // 비밀번호를 BCrypt로 해싱하여 저장
        String hashedPassword = BCrypt.hashpw(member.getPassword(), BCrypt.gensalt());
        member.setPassword(hashedPassword);
        memberRepository.save(member);
        return member.getId();
    }

    // [R] 조회
    public List<Member> findAll() {
        return memberRepository.findAll();
    }

    public Member findById(Long memberId) {
        return memberRepository.findById(memberId);
    }

    // [U] 수정(변경 감지 사용 불가 -> 명시적 저장 필요)
    @Transactional
    public void update(Long id, String newName, String newPassword) {
        Member member = memberRepository.findById(id);

        // 데이터 수정
        member.setUsername(newName);
        if (newPassword != null && !newPassword.isEmpty()) {
            // 비밀번호 변경 시 BCrypt로 해싱하여 저장
            String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
            member.setPassword(hashedPassword);
        }

        // 중요: JPA가 아니므로 Dirty Checking(자동 변경 감지)이 동작하지 않음.
        // 따라서 변경된 객체를 리포지토리에 명시적으로 다시 저장(save)해줘야 파일에 반영됨.
        memberRepository.save(member);
    }

    // [D] 삭제
    @Transactional
    public void delete(Long id) {
        memberRepository.remove(id);
    }


    // 로그인 (BCrypt 비밀번호 검증)
    public String login(String userId, String password) {
        Member member = memberRepository.findByUserId(userId);
        // BCrypt로 해싱된 비밀번호와 입력한 비밀번호를 비교
        if (member != null && BCrypt.checkpw(password, member.getPassword())) {
            String token = jwtUtility.generateJwt(member.getUserId(), member.getUsername(), member.getRoleType());
            return token;
        }
        return "아이디와 비밀번호를 확인하세요";
    }


    public Member findByUserId(String userId){
        return memberRepository.findByUserId(userId);
    }


}
