package com.example.demo.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 회원 관련 요청/응답 DTO를 하나로 모아놓은 클래스입니다.
 *
 * - Request.*  : 클라이언트에서 "입력용"으로 보내는 데이터(ex. 회원가입 폼 등)
 * - Response.* : 서버에서 "출력용"으로 내보내는 데이터(ex. 회원 목록 조회 등)
 * - Result<T>  : 응답을 감싸주는 공통 포맷(wrapper)
 *
 * ── ❗ 초보자를 위한 주석 가이드 ──
 *
 * 1. @NoArgsConstructor (기본 생성자)
 *    → "입력(Request)"에 꼭 필요!
 *    → 이유: Spring/Jackson 같은 라이브러리(기계)가 JSON → 객체로 만들 때
 *            "일단 빈 객체부터 new 한다음 하나씩 집어넣음"
 *    → 이게 없으면 역직렬화 시 에러남!
 *
 * 2. @AllArgsConstructor (전체 필드 생성자)
 *    → "출력(Response)"에 아주 편리!
 *    → 이유: 개발자(사람)가 응답 DTO 만들 때
 *            "new Member(id, username, name)" 딱 한 번에 꽉 채워서 만듦
 *    → 일일이 Setter 호출해야 하는 불편함/실수 방지용!
 *
 * 3. @Data
 *    → Getter/Setter/ToString 등 자동 생성
 *    → 단, 생성자는 직접 컨트롤 해야 해서 @NoArgsConstructor, @AllArgsConstructor 따로 붙임
 */

public class MemberDTO {

    private MemberDTO() {
        // 이 클래스는 오직 static 내부클래스용! 인스턴스 금지(유틸성)
    }

    /** ーーーーーー 🚩 요청(Request) DTO 모음 ーーーーーー */
    public static class Request {
        /**
         * 회원 가입 등 클라이언트 → 서버로 데이터 보낼 때(입력용)
         *
         * 주의! 내가 new로 만들지는 거의 없음.
         * Jackson(라이브러리)이 알아서 기본생성자 + Setter 써서 채움.
         */
        @Data
        @NoArgsConstructor // ⭐ 필수! (입력용: JSON → 객체 변환에 필요)
        public static class Create {
            private String userId;
            private String password;
            private String username;
        }

        /**
         * 회원 정보 수정용
         */
        @Data
        @NoArgsConstructor // ⭐ 필수! (기계가 new 할 용도)
        public static class Update {
            private String username;
            private String password;
        }

        /**
         * 로그인 요청용
         */
        @Data
        @NoArgsConstructor // ⭐ 필수!
        public static class Login {
            private String userId;
            private String password;
        }
    }

    /** ーーーーーー 🚩 응답(Response) DTO 모음 ーーーーーー */
    public static class Response {
        /**
         * 사용자 목록, 단일 조회 등 API 응답용
         *
         * 개발자가 new 하여 값을 "한 번에" 채워서 전달할 때 씀
         */
        @Data
        @AllArgsConstructor // ⭐ 필수! (출력용: 개발자가 바로 값 순서대로 생성)
        public static class Member {
            private Long id;
            private String userId;
            private String username; // 비밀번호는 노출X
        }
    }

    /** ーーーーーー 🚩 공통 응답 래퍼 클래스 ーーーーーー */
    @Data
    @AllArgsConstructor // ⭐ 필수! (출력용 래퍼: 값 한번에 넣으려면)
    public static class Result<T> {
        private T data;
    }
}
