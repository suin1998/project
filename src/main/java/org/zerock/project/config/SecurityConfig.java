package org.zerock.project.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor

public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        // 1. CORS, CSRF, FormLogin, Basic 설정 해제
        http.cors(cors -> cors.configurationSource(request -> {
            CorsConfiguration config = new CorsConfiguration();
            // ... (기존 CORS 설정 유지)
            config.setAllowCredentials(true);
            config.setAllowedOriginPatterns(List.of("*"));
            config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
            config.setAllowedHeaders(List.of("*"));
            return config;
        }));

        http.csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable);

        // 2. 세션 사용 안함 (STATELESS)
        http.sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        // 3. 인증 실패 처리
        http.exceptionHandling(exception ->
                exception.authenticationEntryPoint(jwtAuthenticationEntryPoint)
        );

        // 4. URL 인가 설정 (가장 중요)
        http.authorizeHttpRequests(authorize -> authorize

                // A. 정적 리소스 및 공개 API/페이지 (인증 불필요)
                .requestMatchers(
                        // 정적 리소스
                        "/css/**", "/js/**", "/img/**", "/image/**", "/images/**", "/static/**", "/sub_file/**",
                        "/favicon.ico",
                        // 공개 API 및 페이지
                        "/", "/main", "/home", "/login", "/join", "/community/**", "/board",
                        "/AICoordinator", "/AI", "/search/tags", "/AI/weather", "/MyCloset","/post/**",
                        "/board/**",
                        "/auth/signup", "/auth/login", "/auth/health", "/auth/**", "/MyPage" // auth 하위 모든 경로는 허용
                ).permitAll()

                // 🚨 B. 관리자 페이지 및 API는 ADMIN 권한 필요 (새로 추가)
                .requestMatchers("/admin/**", "/api/admin/**").hasRole("ADMIN")

                // B. 마이페이지는 인증된 사용자만 접근 허용 (수정된 부분)
                // MyCloset 버튼 경로가 마이페이지라면 이 설정이 필요합니다.
                .requestMatchers("/MyPage/**", "/api/**").authenticated()

                // C. 나머지 모든 요청은 인증 필요 (AnyRequest)
                .anyRequest().authenticated()
        );

        // 5. JWT 필터 등록
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
