package com.study.springbootdeveloper.dto.response;

import com.study.springbootdeveloper.type.Category;
import com.study.springbootdeveloper.type.DifficultyType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ProgressResponse {

    private Category category;
    private DifficultyType difficulty;
    private Long totalProblems;
    private Long solvedProblems;
    private Long correctCount;
    private Double accuracy; // 정답률 (%)

    public static ProgressResponse of(Category category, DifficultyType difficulty,
                                      long totalProblems, long solvedProblems, long correctCount) {
        double accuracy = solvedProblems > 0
                ? (correctCount * 100.0) / solvedProblems
                : 0.0;

        return ProgressResponse.builder()
                .category(category)
                .difficulty(difficulty)
                .totalProblems(totalProblems)
                .solvedProblems(solvedProblems)
                .correctCount(correctCount)
                .accuracy(Math.round(accuracy * 100.0) / 100.0)
                .build();
    }
}