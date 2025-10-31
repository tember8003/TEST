package com.study.springbootdeveloper.controller;

import com.study.springbootdeveloper.domain.SolvedProblem;
import com.study.springbootdeveloper.dto.response.*;
import com.study.springbootdeveloper.service.StatisticsService;
import com.study.springbootdeveloper.type.Category;
import com.study.springbootdeveloper.type.DifficultyType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Statistics", description = "학습 통계 API")
@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    /**
     * 전체 통계 조회
     */
    @Operation(summary = "전체 통계 조회", description = "사용자의 전체 학습 통계를 조회합니다.")
    @GetMapping("/user/{userId}")
    public ResponseEntity<OverallStatsResponse> getOverallStats(@PathVariable Long userId) {
        OverallStatsResponse stats = statisticsService.getOverallStats(userId);
        return ResponseEntity.ok(stats);
    }

    /**
     * 카테고리별 통계 조회
     */
    @Operation(summary = "카테고리별 통계", description = "모든 카테고리의 학습 통계를 조회합니다.")
    @GetMapping("/user/{userId}/category")
    public ResponseEntity<List<CategoryStatsResponse>> getCategoryStats(@PathVariable Long userId) {
        List<CategoryStatsResponse> stats = statisticsService.getCategoryStats(userId);
        return ResponseEntity.ok(stats);
    }

    /**
     * 특정 카테고리+난이도 통계 조회
     */
    @Operation(summary = "세부 통계 조회", description = "특정 카테고리와 난이도의 진행도를 조회합니다.")
    @GetMapping("/user/{userId}/category/{category}/difficulty/{difficulty}")
    public ResponseEntity<ProgressResponse> getCategoryDifficultyStats(
            @PathVariable Long userId,
            @PathVariable Category category,
            @PathVariable DifficultyType difficulty
    ) {
        ProgressResponse stats = statisticsService.getCategoryDifficultyStats(userId, category, difficulty);
        return ResponseEntity.ok(stats);
    }

    /**
     * 취약점 분석 (정답률 낮은 순)
     */
    @Operation(summary = "취약점 분석", description = "정답률이 낮은 카테고리/난이도를 정렬하여 반환합니다.")
    @GetMapping("/user/{userId}/weak-points")
    public ResponseEntity<List<WeakPointResponse>> getWeakPoints(@PathVariable Long userId) {
        List<WeakPointResponse> weakPoints = statisticsService.getWeakPoints(userId);
        return ResponseEntity.ok(weakPoints);
    }

    /**
     * 오답 노트 (틀린 문제만)
     */
    @Operation(summary = "오답 노트", description = "틀린 문제만 모아서 조회합니다.")
    @GetMapping("/user/{userId}/wrong-answers")
    public ResponseEntity<List<SolvedProblemResponse>> getWrongAnswers(@PathVariable Long userId) {
        List<SolvedProblem> wrongAnswers = statisticsService.getWrongAnswers(userId);

        List<SolvedProblemResponse> responses = wrongAnswers.stream()
                .map(SolvedProblemResponse::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }
}