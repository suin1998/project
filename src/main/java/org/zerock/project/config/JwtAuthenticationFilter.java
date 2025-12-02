package org.zerock.project.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService; // 필요
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.zerock.project.service.JwtService; // 필요

import io.jsonwebtoken.ExpiredJwtException; // 예외 처리용 import
import io.jsonwebtoken.MalformedJwtException; // 예외 처리용 import

import java.io.IOException;

@Component //spring bean 등록 후 security 사용
@RequiredArgsConstructor // JwtService, UserDetailsService 주입을 위한 Lombok 어노테이션
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter { //딱 한번 실행 보장

    // 1️⃣ 의존성 주입
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService; // SecurityConfig에서 Bean으로 등록된 서비스

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 1. 요청 헤더에서 JWT 추출
        final String jwt = getJwtFromRequest(request);

        if (jwt != null) {
            String userEmail = null;

            try {
                // 2. 토큰에서 이메일(Subject) 추출 시도
                // 이 메서드 내부에서 토큰 서명, 구조, 만료일 검증이 모두 이루어집니다.
                userEmail = jwtService.extractEmail(jwt);

                // 3. 이메일 추출 성공, 그리고 SecurityContext에 아직 인증 정보가 없는 경우
                if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                    // 4. UserDetailsService를 통해 사용자 상세 정보 로드
                    UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

                    // 5. 인증 토큰 생성
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                    // 6. 요청 상세 정보 추가 및 SecurityContext에 설정
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.info("JWT 인증 성공: 이메일 {} 사용자 인증 완료.", userEmail);
                }
            } catch (ExpiredJwtException e) {
                log.warn("만료된 JWT 토큰 감지: {}", e.getMessage());
                // 만료된 토큰 처리 (401 에러는 EntryPoint가 처리)
            } catch (MalformedJwtException e) {
                log.warn("잘못된 형식의 JWT 토큰 감지: {}", e.getMessage());
            } catch (Exception ex) {
                // 그 외 JWT 오류 (서명 오류, UserDetails 로드 오류 등)
                log.error("JWT 처리 또는 사용자 로드 중 오류 발생: {}", ex.getMessage());
            }
        }

        // 다음 필터 체인 실행
        filterChain.doFilter(request, response);
    }

    /**
     * HTTP 요청 헤더에서 JWT를 추출하는 헬퍼 메서드 (Bearer 토큰)
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // "Bearer " (7글자) 이후의 토큰 문자열 반환
        }
        return null;
    }
}