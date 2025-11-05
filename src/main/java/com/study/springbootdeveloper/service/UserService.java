package com.study.springbootdeveloper.service;

import com.study.springbootdeveloper.domain.User;
import com.study.springbootdeveloper.dto.request.UserDto;
import com.study.springbootdeveloper.dto.response.UserResponse;
import com.study.springbootdeveloper.handler.RestApiException;
import com.study.springbootdeveloper.repository.UserRepository;
import com.study.springbootdeveloper.type.ErrorCode;
import com.study.springbootdeveloper.type.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    //회원가입
    public UserResponse signUp(UserDto.SignUp request) {
        log.info("Sign up attempt for loginId: {}", request.getLoginId());

        // 중복 체크
        if (userRepository.findByLoginId(request.getLoginId()).isPresent()) {
            log.warn("LoginId already exists: {}", request.getLoginId());
            throw new RestApiException(ErrorCode.DUPLICATE_LOGIN_ID);
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // User 엔티티 생성
        User user = User.builder()
                .loginId(request.getLoginId())
                .password(encodedPassword)
                .nickname(request.getLoginId()) // 기본값: loginId와 동일
                .role(UserRole.ROLE_USER)
                .build();

        User savedUser = userRepository.save(user);
        log.info("User registered successfully: userId={}, loginId={}, role={}",
                savedUser.getId(), savedUser.getLoginId(), savedUser.getRole());

        return toUserResponse(savedUser);
    }

    //사용자 조회
    @Transactional(readOnly = true)
    public User getUserByLoginId(String loginId) {
        return userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new RestApiException(ErrorCode.USER_NOT_FOUND));
    }

    //userId로 사용자 조회
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RestApiException(ErrorCode.USER_NOT_FOUND));

        return toUserResponse(user);
    }

    //유저 삭제 (ADMIN)
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RestApiException(ErrorCode.USER_NOT_FOUND));
        userRepository.delete(user);
        log.info("User deleted: userId={}", userId);
    }

    private UserResponse toUserResponse(User user) {

        return UserResponse.builder()
                .userId(user.getId())
                .loginId(user.getLoginId())
                .nickname(user.getNickname())
                .solvedProblems(user.getSolvedProblems())
                .createdAt(user.getCreatedAt())
                .build();
    }
}