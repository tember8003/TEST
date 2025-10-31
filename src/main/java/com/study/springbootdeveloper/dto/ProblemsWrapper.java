package com.study.springbootdeveloper.dto;

import com.study.springbootdeveloper.dto.request.ProblemJsonDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ProblemsWrapper {
    private List<ProblemJsonDto> questions;
}