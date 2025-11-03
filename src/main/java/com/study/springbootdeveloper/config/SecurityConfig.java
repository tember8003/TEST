package com.study.springbootdeveloper.config;

import com.study.springbootdeveloper.repository.BlacklistedTokenRepository;
import com.study.springbootdeveloper.repository.RefreshRepository;
import com.study.springbootdeveloper.repository.UserRepository;
import com.study.springbootdeveloper.service.CustomLoginFilter;
import com.study.springbootdeveloper.service.CustomLogoutFilter;
import com.study.springbootdeveloper.service.JwtFilter;
import com.study.springbootdeveloper.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtService jwtService;
    private final RefreshRepository refreshRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final BlacklistedTokenRepository blacklistedTokenRepository;

    public SecurityConfig(JwtService jwtService, RefreshRepository refreshRepository, UserRepository userRepository,
                          PasswordEncoder passwordEncoder, BlacklistedTokenRepository blacklistedTokenRepository) {
        this.jwtService = jwtService;
        this.refreshRepository = refreshRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.blacklistedTokenRepository = blacklistedTokenRepository;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.cors(corsCustomizer -> corsCustomizer.configurationSource(new CorsConfigurationSource() {
            @Override
            public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                CorsConfiguration configuration = new CorsConfiguration();
                configuration.setAllowedOriginPatterns(
                        Arrays.asList("http://localhost:8080", "https://grius.kro.kr")
                );
                configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
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


        // 로그인/로그아웃/JWT/공격탐지 필터
        http.addFilterBefore(
                        new CustomLoginFilter(userRepository, refreshRepository, jwtService, passwordEncoder),
                        UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(
                        new JwtFilter(jwtService, blacklistedTokenRepository, userRepository),
                        UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(
                        new CustomLogoutFilter(jwtService, refreshRepository, blacklistedTokenRepository),
                        LogoutFilter.class);

        // 인가 규칙 (특수 → 일반 순으로! 먼저 매칭되는 규칙이 적용됨)
        http.authorizeHttpRequests(auth -> auth
                // Swagger
                .requestMatchers("/swagger-ui/*", "/v3/api-docs/**").permitAll()

                // CORS preflight
                .requestMatchers(HttpMethod.OPTIONS, "/api/**").permitAll()

                // 관리자 우선
                .requestMatchers("/api/admin/**").hasRole("ADMIN")

                // /api/users 하위에서 'profile'은 보호, 그 외는 공개(순서 중요)
                .requestMatchers("/api/users/profile").hasAnyRole("USER","ADMIN")
                .requestMatchers("/api/users/**").permitAll()

                // 챌린지: 베이스 경로와 슬래시 포함 경로 모두 명시
                .requestMatchers("/api/challenges", "/api/challenges/").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/api/challenges/**").hasAnyRole("USER", "ADMIN")

                // 리더보드/서버/대회시간 공개
                .requestMatchers("/api/leaderboard/**").permitAll()
                .requestMatchers("/api/server-time").permitAll()
                .requestMatchers("/api/contest-time").permitAll()

                // 토큰 재발급, 루트
                .requestMatchers("/api/reissue").permitAll()
                .requestMatchers("/").permitAll()

                // 결제
                .requestMatchers("/api/payment/qr-token").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/api/payment/checkout").hasRole("ADMIN")
                .requestMatchers("/api/payment/history").hasAnyRole("USER", "ADMIN")

                // 팀
                .requestMatchers("/api/team/profile").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/api/team/history").hasAnyRole("USER", "ADMIN")

                // 시그니처: 사용자용 엔드포인트 허용
                .requestMatchers(HttpMethod.POST, "/api/signature/*/check").hasAnyRole("USER","ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/signature/*/unlock").hasAnyRole("USER","ADMIN")
                .requestMatchers(HttpMethod.GET,  "/api/signature/*/status").hasAnyRole("USER","ADMIN")
                .requestMatchers(HttpMethod.GET,  "/api/signature/unlocked").hasAnyRole("USER","ADMIN")
                // 나머지 시그니처는 관리자
                .requestMatchers("/api/signature/**").hasRole("ADMIN")

                // 그 외는 기본 차단(공격 표면 축소)
                .anyRequest().denyAll()
        );

        return http.build();
    }
}