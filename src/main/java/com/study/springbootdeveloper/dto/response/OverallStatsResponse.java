package com.study.springbootdeveloper.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class OverallStatsResponse {

    private Long totalProblems;      // 전체 문제 수
    private Long solvedProblems;     // 푼 문제 수
    private Long correctCount;       // 정답 개수
    private Long incorrectCount;     // 오답 개수
    private Double accuracy;         // 전체 정답률 (%)
    private Double averageScore;     // 평균 점수
}