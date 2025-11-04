package com.study.springbootdeveloper.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.springbootdeveloper.type.Category;
import com.study.springbootdeveloper.type.DifficultyType;
import com.study.springbootdeveloper.type.ProblemType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Table(name = "problems")
public class Problem {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProblemType problemType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DifficultyType difficulty;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String question;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String answer;

    @Column(columnDefinition = "TEXT")
    private String explanation;

    /*
     객관식 선택지를 JSON 문자열로 저장
     예: ["choice1", "choice2", "choice3"]
     */
    @Column(columnDefinition = "TEXT")
    private String choicesJson;

    /*
     choicesJson을 List<String>으로 변환
     */
    public List<String> getChoicesAsList() {
        if (choicesJson == null || choicesJson.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.readValue(choicesJson, new TypeReference<List<String>>() {});
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    /*
     List<String>을 JSON 문자열로 변환하여 저장
     */
    public void setChoicesFromList(List<String> choices) {
        if (choices == null || choices.isEmpty()) {
            this.choicesJson = null;
            return;
        }
        try {
            this.choicesJson = objectMapper.writeValueAsString(choices);
        } catch (JsonProcessingException e) {
            this.choicesJson = null;
        }
    }


    public void updateProblemType(ProblemType problemType) {
        this.problemType = problemType;
    }

    public void updateCategory(Category category) {
        this.category = category;
    }

    public void updateDifficulty(DifficultyType difficulty) {
        this.difficulty = difficulty;
    }

    public void updateQuestion(String question) {
        this.question = question;
    }

    public void updateChoices(String choicesJson) {
        this.choicesJson = choicesJson;
    }

    public void updateAnswer(String answer) {
        this.answer = answer;
    }

    public void updateExplanation(String explanation) {
        this.explanation = explanation;
    }
}