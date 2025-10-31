package com.study.springbootdeveloper.handler;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import com.study.springbootdeveloper.type.ErrorCode;

@Getter
@RequiredArgsConstructor
public class RestApiException extends RuntimeException {

    private final ErrorCode errorCode;
    private final String message;

    public RestApiException(ErrorCode errorCode) {

        super(errorCode.getDescription());
        this.errorCode = errorCode;
        this.message = errorCode.getDescription();
    }
}