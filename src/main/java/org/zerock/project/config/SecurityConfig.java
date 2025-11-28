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
//    @Bean
//    public AuthenticationManager authenticationManager(
//            AuthenticationConfiguration authenticationConfiguration) throws Exception {
//        return authenticationConfiguration.getAuthenticationManager();
//    }
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//
//        // 🔥 CORS 설정 (정상 동작)
//        http.cors(cors -> cors.configurationSource(request -> {
//            CorsConfiguration config = new CorsConfiguration();
//            config.setAllowCredentials(true);
//            config.setAllowedOriginPatterns(List.of("*"));
//            config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
//            config.setAllowedHeaders(List.of("*"));
//            return config;
//        }));
//
//        http.csrf(AbstractHttpConfigurer::disable);
//
//        http.httpBasic(AbstractHttpConfigurer::disable);
//        http.formLogin(AbstractHttpConfigurer::disable);
//
//        // 세션 사용 안함 (JWT 사용)
//        http.sessionManagement(session ->
//                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//        );
//
//        // 인증 실패 → 401 처리
//        http.exceptionHandling(exception ->
//                exception.authenticationEntryPoint(jwtAuthenticationEntryPoint)
//        );
//
//        // 🔥 URL 인가 설정
//        http.authorizeHttpRequests(authorize -> authorize
//
//                .requestMatchers(
//                        "/css/**",
//                        "/js/**",
//                        "/img/**",
//                        "/image/**",
//                        "/images/**",
//                        "/static/**",
//                        "/community",
//                        "/AI"
//                ).permitAll()
//
//                // 2️⃣ 인증 없이 접근 가능한 API
//                .requestMatchers(
//                        "/auth/send-verification-code",
//                        "/auth/verify-code",
//                        "/auth/signup",
//                        "/auth/login",
//                        "/auth/resend-verification",
//                        "/auth/verify-email",
//                        "/auth/health"
//                ).permitAll()
//
//                // 3️⃣ 인증 없이 접근 가능한 페이지
//                .requestMatchers("/", "/main", "/home", "/login", "/join").permitAll()
//
//                // 4️⃣ 나머지 모든 요청은 인증 필요
//                .anyRequest().authenticated()
//        );
//
//        // JWT 필터 등록
//        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
//
//        return http.build();
//    }
//}

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

        // CORS 설정
        http.cors(cors -> cors.configurationSource(request -> {
            CorsConfiguration config = new CorsConfiguration();
            config.setAllowCredentials(true);
            config.setAllowedOriginPatterns(List.of("*"));
            config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
            config.setAllowedHeaders(List.of("*"));
            return config;
        }));

        // CSRF, 기본 로그인 방식 비활성화
        http.csrf(AbstractHttpConfigurer::disable);
        http.httpBasic(AbstractHttpConfigurer::disable);
        http.formLogin(AbstractHttpConfigurer::disable);

        // 세션 사용 안함 (JWT 구조 유지)
        http.sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        // 인증 실패 핸들러 (유지해도 무방)
        http.exceptionHandling(exception ->
                exception.authenticationEntryPoint(jwtAuthenticationEntryPoint)
        );

        // 🔥 여기서 모든 요청을 허용
        http.authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()
        );

        // JWT 필터는 그대로 두어도 됨 (인증이 필요하지 않아서 401은 안 남)
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
