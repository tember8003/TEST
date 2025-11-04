package com.study.springbootdeveloper.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.springbootdeveloper.dto.response.ErrorResponse;
import com.study.springbootdeveloper.type.ErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;


//인증된 사용자가 권한이 없는 리소스에 접근할 때 403 응답 반환
@Slf4j
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {

        log.warn("Access denied: {} - {}",
                request.getRequestURI(),
                accessDeniedException.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(ErrorCode.FORBIDDEN.name())
                .message(ErrorCode.FORBIDDEN.getDescription())
                .build();

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}