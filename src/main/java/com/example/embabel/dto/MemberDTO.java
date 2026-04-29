package com.example.embabel.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MemberDTO {
    private MemberDTO() {

    }

    // -- 요청 dto --//
    public static class Request {
        @Data
        @NoArgsConstructor
        public static class Create {
            private String userId;
            private String password;
            private String username;
        }

        //회원정보 수정
        @Data
        @NoArgsConstructor
        public static class Update {
            private String username;
            private String password;
        }

        //로그인 요청
        @Data
        @NoArgsConstructor
        public static class Login {
            private String userId;
            private String password;
        }
    }

    // -- 응답 dto -- //
    public static class Response {
        @Getter //읽기전용
        @AllArgsConstructor
        public static class Member {
            private Long id;
            private String userId;
            private String username;
        }

    }

    // 공통 응답 레퍼 클래스 //
    @Data
    @AllArgsConstructor
    public static class Result<T> {
        private T data;
    }
}
