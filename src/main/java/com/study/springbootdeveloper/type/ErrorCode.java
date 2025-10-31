package com.study.springbootdeveloper.type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    FORBIDDEN(HttpStatus.FORBIDDEN, "권한이 없습니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    INVALID_REQUEST_FORMAT(HttpStatus.BAD_REQUEST, "잘못된 요청 포맷입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "내부 서버 오류가 발생했습니다."),
    ;

    private final HttpStatus status;
    private final String message;
    public HttpStatus getHttpStatus() {
        return status;
    }
    public String getDescription() {
        return message;
    }
}
