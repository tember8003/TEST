package com.study.springbootdeveloper.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GradingResultDto {

    private boolean isCorrect;

    private int score;

    private String feedback;
}