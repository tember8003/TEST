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

    // User
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),

    // Problem
    PROBLEM_NOT_FOUND(HttpStatus.NOT_FOUND, "문제를 찾을 수 없습니다."),
    PROBLEM_ALREADY_SOLVED(HttpStatus.CONFLICT, "이미 푼 문제입니다."),
    INSUFFICIENT_PROBLEMS(HttpStatus.BAD_REQUEST, "선택한 조건의 문제가 부족합니다."),

    // Session
    SESSION_NOT_FOUND(HttpStatus.NOT_FOUND, "세션을 찾을 수 없습니다."),
    SESSION_ALREADY_COMPLETED(HttpStatus.CONFLICT, "이미 완료된 세션입니다."),

    // SolvedProblem
    SOLVED_NOT_FOUND(HttpStatus.NOT_FOUND, "풀이 기록을 찾을 수 없습니다."),

    // Gemini API
    GEMINI_API_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "AI 피드백 생성 중 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String message;

    public HttpStatus getHttpStatus() {
        return status;
    }

    public String getDescription() {
        return message;
    }
}