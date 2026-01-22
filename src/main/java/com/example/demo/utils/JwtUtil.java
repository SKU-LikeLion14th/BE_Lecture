package com.example.demo.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

/**
 * JWT (Json Web Token) 생성 및 검증 유틸리티 클래스
 * 
 * ── 📚 초보자를 위한 JWT 개념 설명 ──
 * 
 * JWT는 "인증 정보를 담은 토큰"입니다.
 * 예를 들어, 호텔에서 체크인하면 "방 열쇠(토큰)"를 받는 것과 같습니다.
 * 
 * 1. 로그인 성공 → 서버가 JWT 토큰 발급 (방 열쇠 받기)
 * 2. 이후 요청 시 → 헤더에 토큰을 넣어서 보냄 (방 열쇠 보여주기)
 * 3. 서버가 토큰 검증 → 유효하면 요청 처리 (열쇠 확인 후 입장 허용)
 * 
 * JWT 구조: Header.Payload.Signature (점(.)으로 구분된 3부분)
 * - Header: 알고리즘 정보
 * - Payload: 회원 ID, 이름 등 실제 데이터
 * - Signature: 위변조 방지를 위한 서명
 */
public class JwtUtil {

    // 토큰 서명(Signature)을 위한 비밀키. 실무에서는 보안을 위해 환경변수 등으로 관리해야 함.
    private static final String SECRET_KEY = "mySuperSecretKeyForDemonstrationPurposeOnly";

    /**
     * JWT 토큰 생성 메서드
     * 
     * ── 💡 사용 시나리오 ──
     * 로그인 성공 시, 이 메서드를 호출하여 토큰을 만들어 클라이언트에게 전달합니다.
     * 
     * @param memberId 회원 ID (Primary Key) - 토큰에 담을 회원 식별자
     * @param name     회원 이름 - 토큰에 담을 회원 이름
     * @return 생성된 JWT String (예: "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
     */
    public static String createToken(Long memberId, String name) {
        long expireTimeMs = 1000 * 60 * 60; // 토큰 만료 시간: 1시간 (밀리초 단위)
        try {
            // JWT는 Header.Payload.Signature 구조로 구성됨
            return Jwts.builder()
                    // ── Payload (Claims): 토큰에 담을 정보 ──
                    .claim("memberId", memberId) // 비공개 클레임: 회원 ID 저장
                    .claim("name", name) // 비공개 클레임: 회원 이름 저장
                    .setIssuedAt(new Date()) // 발행 시간 (iat) - 토큰이 언제 만들어졌는지
                    .setExpiration(new Date(System.currentTimeMillis() + expireTimeMs)) // 만료 시간 (exp) - 1시간 후

                    // ── Signature (서명): 위변조 방지 ──
                    // 알고리즘: HS256 (HMAC with SHA-256)
                    // Secret Key: 바이트 배열로 변환하여 사용 (Java 21 호환성 문제 해결을 위해 .getBytes 사용)
                    // 이 비밀키로 서명하면, 나중에 토큰이 변조되었는지 확인할 수 있습니다.
                    .signWith(SignatureAlgorithm.HS256, SECRET_KEY.getBytes("UTF-8"))
                    .compact(); // 토큰 생성 및 직렬화 (문자열로 변환)
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * JWT 토큰 검증 메서드
     * 
     * ── 💡 사용 시나리오 ──
     * 클라이언트가 PUT, DELETE 요청을 보낼 때, 헤더에 있는 토큰이 유효한지 확인합니다.
     * 
     * ── 🔍 검증 과정 ──
     * 1. 토큰이 null이거나 비어있으면 → false (토큰 없음)
     * 2. 토큰 서명이 비밀키와 일치하는지 확인 → 일치하지 않으면 예외 발생 (위변조 감지)
     * 3. 토큰 만료 시간 확인 → 만료되었으면 예외 발생
     * 4. 모든 검증 통과 → true (유효한 토큰)
     * 
     * @param token 검증할 JWT 토큰 문자열
     * @return true: 유효한 토큰, false: 유효하지 않은 토큰 (null, 빈 문자열, 파싱 실패 등)
     */
    public static boolean validateToken(String token) {
        // ── 1단계: 토큰 존재 여부 확인 ──
        if (token == null || token.trim().isEmpty()) {
            // 토큰이 없거나 빈 문자열이면 유효하지 않음
            return false;
        }

        try {
            // ── 2단계: 토큰 파싱 및 검증 ──
            // setSigningKey(): 토큰을 만들 때 사용한 비밀키로 검증
            // parseClaimsJws(): 토큰을 파싱하면서 자동으로 검증
            // - 서명이 맞는지 확인
            // - 만료 시간이 지났는지 확인
            // - 모든 검증 통과하면 Claims 객체 반환
            Claims claims = Jwts.parser()
                    .setSigningKey(SECRET_KEY.getBytes("UTF-8"))
                    .parseClaimsJws(token)
                    .getBody();

            // ── 3단계: 만료 시간 추가 확인 (이중 체크) ──
            // parseClaimsJws()가 이미 만료 시간을 확인하지만, 명시적으로 한 번 더 확인
            Date expiration = claims.getExpiration();
            if (expiration.before(new Date())) {
                // 만료 시간이 현재 시간보다 이전이면 → 토큰 만료
                return false;
            }

            // 모든 검증 통과! 유효한 토큰입니다.
            return true;

        } catch (Exception e) {
            // ── 예외 발생 시 유효하지 않은 토큰으로 판단 ──
            // 예외가 발생하는 경우:
            // 1. 토큰 형식이 잘못됨 (점(.)이 2개가 아님 등)
            // 2. 서명이 맞지 않음 (위변조됨)
            // 3. 만료 시간이 지남
            // 4. 기타 파싱 오류
            return false;
        }
    }

    /**
     * JWT 토큰에서 회원 ID 추출 메서드
     * 
     * ── 💡 사용 시나리오 ──
     * 토큰이 유효하다는 것을 확인한 후, 토큰 안에 저장된 회원 ID를 꺼내서 사용합니다.
     * 예: "이 회원이 자신의 정보를 수정하려는 건지 확인"할 때 사용
     * 
     * ── ⚠️ 주의사항 ──
     * 이 메서드를 호출하기 전에 반드시 validateToken()으로 토큰이 유효한지 확인해야 합니다!
     * 유효하지 않은 토큰에서 ID를 추출하려고 하면 예외가 발생할 수 있습니다.
     * 
     * @param token JWT 토큰 문자열
     * @return 토큰에 저장된 회원 ID (Long 타입)
     * @throws Exception 토큰 파싱 실패 시 예외 발생
     */
    public static Long getMemberIdFromToken(String token) throws Exception {
        try {
            // ── 토큰 파싱하여 Claims 객체 가져오기 ──
            Claims claims = Jwts.parser()
                    .setSigningKey(SECRET_KEY.getBytes("UTF-8"))
                    .parseClaimsJws(token)
                    .getBody();

            // ── Claims에서 "memberId" 클레임 추출 ──
            // ⭐ 중요한 포인트: JSON은 모든 숫자를 기본적으로 특정 타입으로 고정하지 않습니다.
            // library에 따라 Integer로 줄 수도, Long으로 줄 수도 있습니다.
            // 따라서 'Number'라는 조상 클래스를 사용하여 꺼낸 뒤 .longValue()로 변환하는 것이 가장 안전합니다.
            Object memberIdObj = claims.get("memberId");
            if (memberIdObj == null) {
                throw new Exception("토큰에 memberId가 없습니다.");
            }

            if (memberIdObj instanceof Number) {
                return ((Number) memberIdObj).longValue();
            } else {
                // 숫자가 아닌 다른 타입(문자열 등)으로 들어온 경우 예외 처리
                throw new Exception("memberId 타입이 올바르지 않습니다: " + memberIdObj.getClass().getSimpleName());
            }

        } catch (Exception e) {
            // 토큰 파싱 실패 시 예외를 그대로 던짐 (호출하는 쪽에서 처리)
            throw new Exception("토큰에서 회원 ID를 추출하는데 실패했습니다: " + e.getMessage(), e);
        }
    }
}
