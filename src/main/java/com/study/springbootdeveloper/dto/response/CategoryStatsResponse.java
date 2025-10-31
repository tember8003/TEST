package com.study.springbootdeveloper.dto.response;

import com.study.springbootdeveloper.type.Category;
import com.study.springbootdeveloper.type.DifficultyType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
@AllArgsConstructor
public class CategoryStatsResponse {

    private Category category;
    private Long totalProblems;
    private Long solvedProblems;
    private Long correctCount;
    private Double accuracy;
    private Map<DifficultyType, ProgressResponse> difficultyStats; // 난이도별 세부 통계
}