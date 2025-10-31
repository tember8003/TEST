package com.study.springbootdeveloper.dto.response;

import com.study.springbootdeveloper.domain.SolvedProblem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class SolvedProblemResponse {

    private Long id;
    private Long problemId;
    private String userAnswer;
    private Boolean isCorrect;
    private Integer score;
    private String aiFeedback;
    private LocalDateTime solvedAt;

    public static SolvedProblemResponse from(SolvedProblem solvedProblem) {
        return SolvedProblemResponse.builder()
                .id(solvedProblem.getId())
                .problemId(solvedProblem.getProblem().getId())
                .userAnswer(solvedProblem.getUserAnswer())
                .isCorrect(solvedProblem.getIsCorrect())
                .score(solvedProblem.getScore())
                .aiFeedback(solvedProblem.getAiFeedback())
                .solvedAt(solvedProblem.getSolvedAt())
                .build();
    }
}