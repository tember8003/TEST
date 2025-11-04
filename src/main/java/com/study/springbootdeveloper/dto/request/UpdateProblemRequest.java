package com.study.springbootdeveloper.dto.request;

import com.study.springbootdeveloper.type.Category;
import com.study.springbootdeveloper.type.DifficultyType;
import com.study.springbootdeveloper.type.ProblemType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProblemRequest {

    private ProblemType problemType;
    private Category category;
    private DifficultyType difficulty;
    private String question;
    private List<String> choices;
    private String answer;
    private String explanation;

}