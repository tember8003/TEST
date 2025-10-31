package com.study.springbootdeveloper.config.oauth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class OAuth2FailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Lazy
    private final OAuth2AuthorizationRequestBasedOnCookieRepository authorizationRequestRepository;

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception
    ) throws IOException {

        log.error("OAuth2 인증 실패: {}", exception.getMessage());
        log.error("실패 원인: {}", exception.getClass().getSimpleName());
        log.error("요청 URI: {}", request.getRequestURI());

        if (exception instanceof OAuth2AuthenticationException) {
            OAuth2AuthenticationException oauth2Exception = (OAuth2AuthenticationException) exception;
            log.error("OAuth2 에러 코드: {}", oauth2Exception.getError().getErrorCode());
            log.error("OAuth2 에러 설명: {}", oauth2Exception.getError().getDescription());
        }

        authorizationRequestRepository.removeAuthorizationRequestCookies(request, response);

        //에러 페이지로 리다이렉트 (에러 정보 포함)
        String targetUrl = UriComponentsBuilder.fromUriString("/login")
                .queryParam("error", "true")
                .queryParam("message", exception.getMessage())
                .build()
                .toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

}