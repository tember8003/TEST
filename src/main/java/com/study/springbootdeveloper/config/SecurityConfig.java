package com.study.springbootdeveloper.config;

import com.study.springbootdeveloper.repository.BlacklistedTokenRepository;
import com.study.springbootdeveloper.repository.RefreshRepository;
import com.study.springbootdeveloper.repository.UserRepository;
import com.study.springbootdeveloper.service.CustomLoginFilter;
import com.study.springbootdeveloper.service.CustomLogoutFilter;
import com.study.springbootdeveloper.service.JwtFilter;
import com.study.springbootdeveloper.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtService jwtService;
    private final RefreshRepository refreshRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final BlacklistedTokenRepository blacklistedTokenRepository;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // CORS 설정
        http.cors(corsCustomizer -> corsCustomizer.configurationSource(new CorsConfigurationSource() {
            @Override
            public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                CorsConfiguration configuration = new CorsConfiguration();
                configuration.setAllowedOriginPatterns(
                        Arrays.asList("http://localhost:8080", "http://localhost:3000", "https://grius.kro.kr")
                );
                configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
                configuration.setAllowCredentials(true);
                configuration.setAllowedHeaders(Arrays.asList(
                        "Authorization", "Content-Type", "Set-Cookie", "X-Requested-With", "Accept", "Origin"
                ));
                configuration.setExposedHeaders(Arrays.asList("Authorization", "access", "X-Custom-Header"));
                configuration.setMaxAge(3600L);
                return configuration;
            }
        }));

        // Stateless API 기본 설정
        http.csrf(csrf -> csrf.disable());
        http.formLogin(form -> form.disable());
        http.httpBasic(basic -> basic.disable());

        // Session 정책: STATELESS (JWT 기반이므로 세션 사용 안 함)
        http.sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        // 예외 처리 핸들러 설정
        http.exceptionHandling(exception -> exception
                .authenticationEntryPoint(authenticationEntryPoint)  // 401
                .accessDeniedHandler(accessDeniedHandler)           // 403
        );

        // 필터 체인 설정
        http.addFilterBefore(
                        new CustomLoginFilter(userRepository, refreshRepository, jwtService, passwordEncoder),
                        UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(
                        new JwtFilter(jwtService, blacklistedTokenRepository, userRepository),
                        UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(
                        new CustomLogoutFilter(jwtService, refreshRepository, blacklistedTokenRepository),
                        LogoutFilter.class);

        http.authorizeHttpRequests(auth -> auth
                // Swagger (개발용)
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

                // CORS preflight 요청
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // ========== 정적 리소스 및 HTML 페이지 허용 ==========
                .requestMatchers("/", "/login", "/signup", "/category", "/problems", "/challenge").permitAll()
                .requestMatchers("/css/**", "/js/**", "/images/**", "/favicon.ico").permitAll()

                // ========== 어드민 페이지 (ADMIN만 접근) ==========
                .requestMatchers("/admin", "/statistics").hasRole("ADMIN")

                // 인증 관련 (회원가입, 로그인)
                .requestMatchers("/api/users/sign-up", "/api/users/sign-in").permitAll()

                // 토큰 재발급
                .requestMatchers("/api/reissue").permitAll()

                // 문제 조회
                .requestMatchers(HttpMethod.GET, "/api/problems/**").permitAll()
                // 문제 답안 제출 (비로그인도 가능)
                .requestMatchers(HttpMethod.POST, "/api/problems/submit").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/problems/solved/**").hasAnyRole("USER", "ADMIN")

                // 세션 생성 및 관리 (로그인 필요)
                .requestMatchers("/api/sessions/**").hasAnyRole("USER","ADMIN")

                // === 통계 관련 API ===
                .requestMatchers("/api/stats/**").hasAnyRole("USER", "ADMIN")

                // === 관리자 API ===
                .requestMatchers("/api/admin/**").hasRole("ADMIN")

                // 나머지 모든 요청: 인증 필요
                .anyRequest().authenticated()
        );

        return http.build();
    }
}