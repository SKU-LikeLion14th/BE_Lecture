package com.example.demo.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Member {

    private Long id;

    private String username; // 로그인id
    private String password;
    private String name; // 사용자 이름

    //@NoArgsConstructor 어노테이션 덕분에 기본 생성자를 일일이 쓸 필요x
/*    public Member() {
    }*/

    public Member(Long id, String username, String password, String name) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.name = name;
    }
}
