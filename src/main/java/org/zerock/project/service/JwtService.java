package org.zerock.project.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys; // import 추가
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Slf4j
@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    // application.yml에 jwt.expiration 설정이 있다면 사용
    @Value("${jwt.expiration}")
    private Long expiration;

    // application.yml에 jwt.refresh-expiration 설정이 있다면 사용
    @Value("${jwt.refresh-expiration}")
    private Long refreshExpiration;

    // application.yml에 jwt.expiration-ms 설정이 있다면 사용
    // 이 필드는 사용하지 않거나, Access Token 만료 시간으로 통일하는 것이 좋습니다.
    // 현재는 아래 createToken 로직에서 사용되지 않도록 수정되었습니다.
    @Value("${jwt.expiration-ms}")
    private long expirationTime;

    /**
     * SecretKey 생성 (Base64 인코딩된 문자열을 키로 변환)
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Access Token 생성
     */
    public String generateAccessToken(String email) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, email, expiration);
    }

    /**
     * Refresh Token 생성
     */
    public String generateRefreshToken(String email) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, email, refreshExpiration);
    }

    /**
     * JWT 토큰 생성 (Subject와 Expiration을 사용)
     */
    private String createToken(Map<String, Object> claims, String subject, Long expiration) {

        return Jwts.builder()
                .setClaims(claims)
                // 1. userDetails 대신 파라미터 subject (email) 사용
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                // 2. 클래스 필드 expirationTime 대신 파라미터 expiration 사용
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                // 3. compact()에 인수를 제거합니다.
                .compact();
    }

    /**
     * 토큰에서 이메일 추출 (Subject 추출)
     */
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * 토큰에서 만료일 추출
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * 토큰에서 특정 클레임 추출
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * 토큰에서 모든 클레임 추출 (파싱)
     */
    private Claims extractAllClaims(String token) {
        try {
            // Jwts.parser() 최신 방식 적용
            return Jwts.parser()
                    .verifyWith(getSigningKey()) // 키 설정
                    .build()
                    .parseSignedClaims(token) // 토큰 파싱
                    .getPayload(); // 클레임 추출
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
            throw e;
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
            throw e;
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            throw e;
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * 토큰 만료 여부 확인
     */
    public Boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    /**
     * 토큰 유효성 검증
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String email = extractEmail(token);
            // userDetails.getUsername()은 일반적으로 principal (여기서는 이메일)을 반환합니다.
            return (email.equals(userDetails.getUsername()) && !isTokenExpired(token));
        } catch (Exception e) {
            log.error("JWT validation error: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 토큰 유효성 검증 (이메일로)
     */
    public Boolean validateToken(String token, String email) {
        try {
            final String tokenEmail = extractEmail(token);
            return (tokenEmail.equals(email) && !isTokenExpired(token));
        } catch (Exception e) {
            log.error("JWT validation error: {}", e.getMessage());
            return false;
        }
    }
}