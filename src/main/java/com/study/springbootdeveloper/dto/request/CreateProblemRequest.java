package com.study.springbootdeveloper.dto.request;

import com.study.springbootdeveloper.type.Category;
import com.study.springbootdeveloper.type.DifficultyType;
import com.study.springbootdeveloper.type.ProblemType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateProblemRequest {

    @NotNull(message = "문제 유형은 필수입니다.")
    private ProblemType problemType;

    @NotNull(message = "카테고리는 필수입니다.")
    private Category category;

    @NotNull(message = "난이도는 필수입니다.")
    private DifficultyType difficulty;

    @NotBlank(message = "문제 내용은 필수입니다.")
    private String question;

    private List<String> choices; // 객관식인 경우만 사용

    @NotBlank(message = "정답은 필수입니다.")
    private String answer;

    @NotBlank(message = "해설은 필수입니다.")
    private String explanation;
}