package com.study.springbootdeveloper.controller;

import com.study.springbootdeveloper.domain.User;
import com.study.springbootdeveloper.dto.request.UserDto;
import com.study.springbootdeveloper.dto.response.UserResponse;
import com.study.springbootdeveloper.handler.RestApiException;
import com.study.springbootdeveloper.service.UserService;
import com.study.springbootdeveloper.type.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "User", description = "사용자 인증 API")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "내 프로필 조회", description = "JWT 토큰으로 현재 로그인한 사용자 정보를 조회합니다.")
    @GetMapping("/profile")
    public ResponseEntity<UserResponse> getMyProfile(HttpServletRequest request){
        Long userId = (Long) request.getAttribute("userId");

        if (userId == null) {
            throw new RestApiException(ErrorCode.USER_NOT_FOUND);
        }

        UserResponse userResponse = userService.getUserById(userId);

        return ResponseEntity.ok(userResponse);
    }

    //회원가입
    @Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다.")
    @PostMapping("/sign-up")
    public ResponseEntity<UserResponse> signUp(@Valid @RequestBody UserDto.SignUp request) {
        UserResponse userResponse = userService.signUp(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);
    }

    /**
     * 로그인은 CustomLoginFilter에서 처리
     * POST /api/users/sign-in
     */

    //사용자 정보 조회
    @Operation(summary = "사용자 정보 조회", description = "특정 사용자의 정보를 조회합니다.")
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserInfo(@PathVariable Long userId) {

        UserResponse userResponse = userService.getUserById(userId);

        return ResponseEntity.ok(userResponse);
    }
}