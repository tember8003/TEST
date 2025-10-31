package com.study.springbootdeveloper.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SubmitAnswerRequest {

    @NotNull(message = "문제 ID는 필수입니다.")
    private Long problemId;

    @NotBlank(message = "답변은 필수입니다.")
    private String userAnswer;
}