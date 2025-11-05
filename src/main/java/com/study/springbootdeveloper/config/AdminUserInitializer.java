package com.study.springbootdeveloper.config;

import com.study.springbootdeveloper.domain.User;
import com.study.springbootdeveloper.repository.UserRepository;
import com.study.springbootdeveloper.type.UserRole;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminUserInitializer {

    @Value("${SPRING_SECURITY_USER_NAME}")
    private String adminUsername;

    @Value("${SPRING_SECURITY_USER_PASSWORD}")
    private String adminPassword;

    @Bean
    public CommandLineRunner createAdminUser(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (!userRepository.existsByLoginId((adminUsername))) {
                User admin = User.builder()
                        .loginId(adminUsername)
                        .password(passwordEncoder.encode(adminPassword))
                        .nickname(adminUsername)
                        .role(UserRole.ROLE_ADMIN)
                        .build();
                userRepository.save(admin);
                System.out.println("관리자 계정 생성 완료: " + adminUsername);
            }
        };
    }
}