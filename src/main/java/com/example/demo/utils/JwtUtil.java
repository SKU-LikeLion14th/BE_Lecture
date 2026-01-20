package com.example.demo.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

// JWT (Json Web Token) 생성 유틸리티 클래스
// JJWT 라이브러리(0.9.1)를 사용하여 토큰을 생성함
public class JwtUtil {

    // 토큰 서명(Signature)을 위한 비밀키. 실무에서는 보안을 위해 환경변수 등으로 관리해야 함.
    private static final String SECRET_KEY = "mySuperSecretKeyForDemonstrationPurposeOnly";

    /**
     * JWT 토큰 생성 메서드
     * 
     * @param memberId 회원 ID (Primary Key)
     * @param name     회원 이름
     * @return 생성된 JWT String
     */
    public static String createToken(Long memberId, String name) {
        long expireTimeMs = 1000 * 60 * 60; // 토큰 만료 시간: 1시간
        try {
            // JWT는 Header.Payload.Signature 구조로 구성됨
            return Jwts.builder()
                    // Payload (Claims): 토큰에 담을 정보
                    .claim("memberId", memberId) // 비공개 클레임 (ID)
                    .claim("name", name) // 비공개 클레임 (이름)
                    .setIssuedAt(new Date()) // 발행 시간 (iat)
                    .setExpiration(new Date(System.currentTimeMillis() + expireTimeMs)) // 만료 시간 (exp)

                    // Signature (서명): 위변조 방지
                    // 알고리즘: HS256 (HMAC with SHA-256)
                    // Secret Key: 바이트 배열로 변환하여 사용 (Java 21 호환성 문제 해결을 위해 .getBytes 사용)
                    .signWith(SignatureAlgorithm.HS256, SECRET_KEY.getBytes("UTF-8"))
                    .compact(); // 토큰 생성 및 직렬화
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
