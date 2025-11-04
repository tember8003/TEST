package com.study.springbootdeveloper.controller;

import com.study.springbootdeveloper.domain.User;
import com.study.springbootdeveloper.dto.request.UserDto;
import com.study.springbootdeveloper.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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

    //회원가입
    @Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다.")
    @PostMapping("/sign-up")
    public ResponseEntity<Map<String, Object>> signUp(@Valid @RequestBody UserDto.SignUp request) {
        User user = userService.signUp(request);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "회원가입 성공!");
        response.put("userId", user.getId());
        response.put("loginId", user.getLoginId());
        response.put("role", user.getRole().name());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 로그인은 CustomLoginFilter에서 처리
     * POST /api/users/sign-in
     */

    //사용자 정보 조회
    @Operation(summary = "사용자 정보 조회", description = "특정 사용자의 정보를 조회합니다.")
    @GetMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> getUserInfo(@PathVariable Long userId) {
        User user = userService.getUserById(userId);

        Map<String, Object> response = new HashMap<>();
        response.put("userId", user.getId());
        response.put("loginId", user.getLoginId());
        response.put("nickname", user.getNickname());
        response.put("role", user.getRole().name());
        response.put("createdAt", user.getCreatedAt());

        return ResponseEntity.ok(response);
    }
}