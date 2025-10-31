package com.study.springbootdeveloper.dto.request;

import com.study.springbootdeveloper.type.Category;
import com.study.springbootdeveloper.type.DifficultyType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateSessionRequest {

    @NotNull(message = "카테고리는 필수입니다.")
    private Category category;

    @NotNull(message = "난이도는 필수입니다.")
    private DifficultyType difficulty;

    @Min(value = 1, message = "문제 개수는 최소 1개 이상이어야 합니다.")
    private Integer totalQuestions = 10;

    private Integer timeLimitMinutes;
}