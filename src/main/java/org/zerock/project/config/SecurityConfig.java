//package org.zerock.project.config;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
//import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//import org.springframework.web.cors.CorsConfiguration;
//
//import java.util.List;
//
//@Configuration
//@EnableWebSecurity
//@EnableMethodSecurity
//@RequiredArgsConstructor
//public class SecurityConfig {
//
//    private final JwtAuthenticationFilter jwtAuthenticationFilter;
//    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//
//
//
//    @Bean
//    public AuthenticationManager authenticationManager(
//            AuthenticationConfiguration authenticationConfiguration) throws Exception {
//        return authenticationConfiguration.getAuthenticationManager();
//    }
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//
//        // 1. CORS, CSRF, FormLogin, Basic 설정 해제
//        http.cors(cors -> cors.configurationSource(request -> {
//            CorsConfiguration config = new CorsConfiguration();
//            // ... (기존 CORS 설정 유지)
//            config.setAllowCredentials(true);
//            config.setAllowedOriginPatterns(List.of("*"));
//            config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
//            config.setAllowedHeaders(List.of("*"));
//            return config;
//        }));
//
//        http.csrf(AbstractHttpConfigurer::disable)
//                .httpBasic(AbstractHttpConfigurer::disable)
//                .formLogin(AbstractHttpConfigurer::disable);
//
//        // 2. 세션 사용 안함 (STATELESS)
//        http.sessionManagement(session ->
//                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//        );
//
//        // 3. 인증 실패 처리
//        http.exceptionHandling(exception ->
//                exception.authenticationEntryPoint(jwtAuthenticationEntryPoint)
//        );
//
//        // 4. URL 인가 설정 (가장 중요)
//        http.authorizeHttpRequests(authorize -> authorize
//
//                // ★ 정적 리소스 허용
//                .requestMatchers(
//                        "/css/**", "/js/**", "/img/**", "/image/**", "/images/**", "/static/**"
//                ).permitAll()
//
//                // ★ HTML 페이지 접근 허용 (로그인 없이 열리는 페이지들)
//                .requestMatchers(
//                        "/", "/main", "/home", "/login", "/join", "/community", "/AICoordinator",
//                        "/AI", "/myCloset"
//                ).permitAll()
//
//                // ★ 인증 없이 호출 가능한 auth 관련 API
//                .requestMatchers(
//                        "/auth/signup", "/auth/login", "/auth/health", "/auth/**"
//                ).permitAll()
//
//                // ★ 그 외 API는 인증 필요
//                .requestMatchers("/api/**").authenticated()
//
//                // 나머지 전부 허용
//                .anyRequest().permitAll()
//        );
//
//        // 5. JWT 필터 등록
//        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
//
//        return http.build();
//    }
//}