package com.study.springbootdeveloper.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.study.springbootdeveloper.domain.Session;
import com.study.springbootdeveloper.type.Category;
import com.study.springbootdeveloper.type.DifficultyType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SessionResponse {

    private Long id;
    private Category category;
    private DifficultyType difficulty;
    private Integer totalQuestions;
    private Integer correctCount;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private Integer timeLimitMinutes;
    private Double accuracy; // 정답률 (%)

    public static SessionResponse from(Session session) {
        double accuracy = session.getTotalQuestions() > 0
                ? (session.getCorrectCount() * 100.0) / session.getTotalQuestions()
                : 0.0;

        return SessionResponse.builder()
                .id(session.getId())
                .category(session.getCategory())
                .difficulty(session.getDifficulty())
                .totalQuestions(session.getTotalQuestions())
                .correctCount(session.getCorrectCount())
                .startedAt(session.getStartedAt())
                .completedAt(session.getCompletedAt())
                .timeLimitMinutes(session.getTimeLimitMinutes())
                .accuracy(Math.round(accuracy * 100.0) / 100.0) // 소수점 2자리
                .build();
    }
}