package com.study.springbootdeveloper.controller;

import com.study.springbootdeveloper.domain.Problem;
import com.study.springbootdeveloper.domain.SolvedProblem;
import com.study.springbootdeveloper.dto.request.SubmitAnswerRequest;
import com.study.springbootdeveloper.dto.response.ProblemResponse;
import com.study.springbootdeveloper.dto.response.SolvedProblemResponse;
import com.study.springbootdeveloper.service.ProblemService;
import com.study.springbootdeveloper.service.SolvingService;
import com.study.springbootdeveloper.type.Category;
import com.study.springbootdeveloper.type.DifficultyType;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/problems")
@RequiredArgsConstructor
public class ProblemController {

    private final ProblemService problemService;
    private final SolvingService solvingService;

    /**
     * 모든 문제 조회
     */
    @GetMapping
    public ResponseEntity<List<ProblemResponse>> getAllProblems(
            @RequestParam(required = false) Category category,
            @RequestParam(required = false) DifficultyType difficulty,
            @RequestParam(required = false) Long userId
    ) {
        List<Problem> problems;

        if (category != null && difficulty != null) {
            problems = problemService.getProblemsByCategoryAndDifficulty(category, difficulty);
        } else if (category != null) {
            problems = problemService.getProblemsByCategory(category);
        } else if (difficulty != null) {
            problems = problemService.getProblemsByDifficulty(difficulty);
        } else {
            problems = problemService.getAllProblems();
        }

        List<ProblemResponse> responses = problems.stream()
                .map(problem -> {
                    if (userId != null) {
                        boolean isSolved = problemService.isSolvedByUser(userId, problem.getId());
                        return ProblemResponse.from(problem, isSolved);
                    }
                    return ProblemResponse.from(problem);
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    /**
     * 특정 문제 상세 조회
     */
    @GetMapping("/{problemId}")
    public ResponseEntity<ProblemResponse> getProblem(
            @PathVariable Long problemId,
            @RequestParam(required = false) Long userId
    ) {
        Problem problem = problemService.getProblemById(problemId);

        ProblemResponse response;
        if (userId != null) {
            boolean isSolved = problemService.isSolvedByUser(userId, problemId);
            response = ProblemResponse.from(problem, isSolved);
        } else {
            response = ProblemResponse.from(problem);
        }

        return ResponseEntity.ok(response);
    }

    /**
     * 답안 제출 (자유 선택 모드)
     */
    @PostMapping("/submit")
    public ResponseEntity<SolvedProblemResponse> submitAnswer(
            @RequestParam(required = false) Long userId,
            @Valid @RequestBody SubmitAnswerRequest request
    ) {
        SolvedProblem solvedProblem = solvingService.submitAnswer(
                userId,
                request.getProblemId(),
                request.getUserAnswer()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SolvedProblemResponse.from(solvedProblem));
    }

    /**
     * 풀이 기록 조회
     */
    @GetMapping("/solved/{problemId}")
    public ResponseEntity<SolvedProblemResponse> getSolvedProblem(
            @PathVariable Long problemId,
            @RequestParam Long userId
    ) {
        SolvedProblem solvedProblem = solvingService.getSolvedProblem(userId, problemId);
        return ResponseEntity.ok(SolvedProblemResponse.from(solvedProblem));
    }
}