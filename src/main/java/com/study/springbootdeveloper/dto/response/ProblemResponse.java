package com.study.springbootdeveloper.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.study.springbootdeveloper.domain.Problem;
import com.study.springbootdeveloper.type.Category;
import com.study.springbootdeveloper.type.DifficultyType;
import com.study.springbootdeveloper.type.ProblemType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProblemResponse {

    private Long id;
    private ProblemType problemType;
    private Category category;
    private DifficultyType difficulty;
    private String question;
    private List<String> choices; // 객관식일 경우에만
    private String explanation;
    private Boolean isSolved; // 유저가 이미 풀었는지 여부

    public static ProblemResponse from(Problem problem) {
        return ProblemResponse.builder()
                .id(problem.getId())
                .problemType(problem.getProblemType())
                .category(problem.getCategory())
                .difficulty(problem.getDifficulty())
                .question(problem.getQuestion())
                .choices(parseChoices(problem.getChoicesJson()))
                .explanation(problem.getExplanation())
                .build();
    }

    public static ProblemResponse from(Problem problem, boolean isSolved) {
        return ProblemResponse.builder()
                .id(problem.getId())
                .problemType(problem.getProblemType())
                .category(problem.getCategory())
                .difficulty(problem.getDifficulty())
                .question(problem.getQuestion())
                .choices(parseChoices(problem.getChoicesJson()))
                .explanation(problem.getExplanation())
                .isSolved(isSolved)
                .build();
    }

    private static List<String> parseChoices(String choicesJson) {
        if (choicesJson == null || choicesJson.isEmpty()) {
            return null;
        }
        try {
            // JSON 문자열 파싱 (간단하게 처리)
            return List.of(choicesJson.replace("[", "")
                    .replace("]", "")
                    .replace("\"", "")
                    .split(","));
        } catch (Exception e) {
            return null;
        }
    }
}