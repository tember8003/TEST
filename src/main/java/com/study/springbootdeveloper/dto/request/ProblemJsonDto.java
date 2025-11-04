package com.study.springbootdeveloper.dto.request;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.springbootdeveloper.domain.Problem;
import com.study.springbootdeveloper.type.Category;
import com.study.springbootdeveloper.type.DifficultyType;
import com.study.springbootdeveloper.type.ProblemType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ProblemJsonDto {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private Long id;
    private String type;
    private String category;
    private String difficulty;
    private String question;
    private List<String> choices;
    private String answer;
    private String explanation;

    /**
     * DTO -> Entity 변환
     */
    public Problem toEntity() {

        String choicesJson = null;
        if (choices != null && !choices.isEmpty()) {
            try {
                choicesJson = objectMapper.writeValueAsString(choices);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Failed to convert choices to JSON", e);
            }
        }

        return Problem.builder()
                .problemType(parseProblemType(type))
                .category(parseCategory(category))
                .difficulty(parseDifficulty(difficulty))
                .question(question)
                .answer(answer)
                .explanation(explanation)
                .choicesJson(choicesJson)
                .build();
    }

    private ProblemType parseProblemType(String type) {
        return switch (type.toLowerCase()) {
            case "multiple_choice" -> ProblemType.MULTIPLE_CHOICE;
            case "short_answer" -> ProblemType.SHORT_ANSWER;
            case "descriptive" -> ProblemType.DESCRIPTIVE;
            default -> throw new IllegalArgumentException("Invalid problem type: " + type);
        };
    }

    private Category parseCategory(String category) {
        return switch (category.toUpperCase().replace(" ", "_")) {
            case "SPRING_CORE" -> Category.SPRING_CORE;
            case "SPRING_BOOT" -> Category.SPRING_BOOT;
            case "SPRING_MVC" -> Category.SPRING_MVC;
            case "SPRING_DATA_JPA" -> Category.SPRING_DATA_JPA;
            case "SPRING_SECURITY" -> Category.SPRING_SECURITY;
            default -> throw new IllegalArgumentException("Invalid category: " + category);
        };
    }

    private DifficultyType parseDifficulty(String difficulty) {
        return switch (difficulty.toLowerCase()) {
            case "beginner" -> DifficultyType.BEGINNER;
            case "intermediate" -> DifficultyType.INTERMEDIATE;
            case "advanced" -> DifficultyType.ADVANCED;
            default -> throw new IllegalArgumentException("Invalid difficulty: " + difficulty);
        };
    }
}