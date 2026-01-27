package com.example.demo.domain;

import com.example.demo.enums.RoleType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@Entity
public class Member {
    @Id @GeneratedValue
    private Long id;
    @Column(unique = true)
    private String userId; // 로그인id
    private String password;
    private String username; // 사용자 이름

    @Enumerated(EnumType.STRING)
    private RoleType roleType = RoleType.MEMBER;

    //@NoArgsConstructor 어노테이션 덕분에 기본 생성자를 일일이 쓸 필요x
/*    public Member() {
    }*/

    public Member(String userId, String password, String username) {
        this.userId = userId;
        this.password = password;
        this.username = username;
    }
}
