package com.example.demo.security;

import com.example.demo.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.http.HttpMethod;

/**
 * Spring Security 설정 클래스
 * - JWT 기반 인증을 사용하는 REST API 서버를 위한 보안 설정
 * - 세션, 폼 로그인, CSRF를 모두 사용하지 않음
 */
@Configuration          // Spring 설정 클래스임을 나타냄
@EnableWebSecurity      // Spring Security를 활성화하고, 우리가 설정한 보안 설정을 적용
@RequiredArgsConstructor
public class SecurityConfig {

    // JWT 생성/검증을 담당하는 유틸리티 클래스
    private final JwtUtility jwtUtility;

    // JWT 인증 시 사용자 정보를 로드하기 위한 UserDetailsService 구현체
    private final CustomUserDetailsService customUserDetailsService;

    /**
     * JWT 인증 필터 빈
     * - 요청마다 Authorization 헤더의 JWT를 검증
     * - 유효한 경우 SecurityContext에 인증 정보 설정
     */
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtUtility, customUserDetailsService);
    }

    /**
     * Spring Security 필터 체인 설정
     * - 어떤 요청을 허용/차단할지
     * - 어떤 필터가 어떤 순서로 동작할지 정의
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.httpBasic(AbstractHttpConfigurer::disable)     // HTTP Basic 인증 비활성화 (Authorization 헤더 기반 JWT만 사용)
                .csrf(AbstractHttpConfigurer::disable)      // CSRF 비활성화 (STATELESS + JWT 구조에서는 불필요)
                .formLogin(AbstractHttpConfigurer::disable) // Spring Security 기본 로그인 폼 비활성화
                // 세션을 생성하거나 사용하지 않도록 설정 (완전한 JWT 기반)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 요청 URL별 접근 권한 설정
                .authorizeHttpRequests(auth -> auth
                        // 회원가입 / 로그인 / 에러 페이지는 인증 없이 허용
                        .requestMatchers(
                                "/api/members",
                                "/api/login",
                                "/error"
                        ).permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/article/**").permitAll()             // 게시글 조회(GET)은 인증 없이 허용
                        .requestMatchers(HttpMethod.GET, "/api/comment/**").permitAll()             // 댓글 조회(GET)은 인증 없이 허용
                        .requestMatchers("/api/article/**").hasRole("MEMBER")                       // 게시글 작성/수정/삭제는 MEMBER 권한 필요
                        .requestMatchers("/api/comment/**").hasAnyRole("ADMIN", "MEMBER")    // 댓글 작성/수정/삭제는 ADMIN 또는 MEMBER 권한 필요
                        .anyRequest().authenticated()                                                 // 위에서 정의되지 않은 모든 요청은 인증 필요
                )

                // 인증/인가 실패 시 기본 HTML 응답 대신 JSON 응답으로 처리
                .exceptionHandling(exception -> exception
                        // 🔐 인증(Authentication) 실패 처리
                        // - JWT 토큰이 없는 경우
                        // - 토큰이 만료되었거나 위조된 경우
                        // - 인증 자체가 이루어지지 않았을 때 호출됨
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            response.setCharacterEncoding("UTF-8");

                            // REST API 서버이므로 기본 HTML 에러 페이지 대신
                            // JSON 형식의 에러 응답을 반환
                            response.getWriter().write(
                                    "{\"error\":\"UNAUTHORIZED\",\"message\":\"인증이 필요합니다.\"}"
                            );
                        })

                        // 🚫 인가(Authorization) 실패 처리
                        // - 인증은 되었으나(Role/권한은 있음)
                        // - 요청한 API에 접근할 권한이 없는 경우 호출됨
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN); // 403
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            response.setCharacterEncoding("UTF-8");

                            // 권한 부족 시 클라이언트가 명확히 구분할 수 있도록
                            // JSON 형태의 에러 메시지 반환
                            response.getWriter().write(
                                    "{\"error\":\"FORBIDDEN\",\"message\":\"접근 권한이 없습니다.\"}"
                            );
                        })
                )

                // JWT 인증 필터를 UsernamePasswordAuthenticationFilter 앞에 배치
                // → 요청이 들어오면 가장 먼저 JWT 검증을 수행하도록 보장
                .addFilterBefore(
                        jwtAuthenticationFilter(),
                        UsernamePasswordAuthenticationFilter.class
                );

        // 최종적으로 SecurityFilterChain 객체 생성
        return http.build();
    }
}