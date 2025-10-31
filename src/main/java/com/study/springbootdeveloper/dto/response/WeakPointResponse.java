package com.study.springbootdeveloper.dto.response;

import com.study.springbootdeveloper.type.Category;
import com.study.springbootdeveloper.type.DifficultyType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class WeakPointResponse {

    private Category category;
    private DifficultyType difficulty;
    private Long solvedCount;
    private Long correctCount;
    private Double accuracy; // 정답률 (%)
}