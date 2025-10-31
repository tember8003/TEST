package com.study.springbootdeveloper.service;

import com.study.springbootdeveloper.domain.SolvedProblem;
import com.study.springbootdeveloper.dto.response.CategoryStatsResponse;
import com.study.springbootdeveloper.dto.response.OverallStatsResponse;
import com.study.springbootdeveloper.dto.response.ProgressResponse;
import com.study.springbootdeveloper.dto.response.WeakPointResponse;
import com.study.springbootdeveloper.repository.ProblemRepository;
import com.study.springbootdeveloper.repository.SolvedProblemRepository;
import com.study.springbootdeveloper.type.Category;
import com.study.springbootdeveloper.type.DifficultyType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
public class StatisticsService {

    private final SolvedProblemRepository solvedProblemRepository;
    private final ProblemRepository problemRepository;

    public StatisticsService(SolvedProblemRepository solvedProblemRepository,
                             ProblemRepository problemRepository) {
        this.solvedProblemRepository = solvedProblemRepository;
        this.problemRepository = problemRepository;
    }

    /**
     * 전체 통계 조회
     */
    public OverallStatsResponse getOverallStats(Long userId) {
        // 전체 문제 수
        long totalProblems = problemRepository.count();

        // 푼 문제 수
        List<SolvedProblem> solvedProblems = solvedProblemRepository.findByUserId(userId);
        long solvedCount = solvedProblems.size();

        // 정답/오답 개수
        long correctCount = solvedProblems.stream()
                .filter(SolvedProblem::getIsCorrect)
                .count();
        long incorrectCount = solvedCount - correctCount;

        // 전체 정답률
        double accuracy = solvedCount > 0
                ? (correctCount * 100.0) / solvedCount
                : 0.0;

        // 평균 점수
        double averageScore = solvedProblems.stream()
                .mapToInt(SolvedProblem::getScore)
                .average()
                .orElse(0.0);

        return OverallStatsResponse.builder()
                .totalProblems(totalProblems)
                .solvedProblems(solvedCount)
                .correctCount(correctCount)
                .incorrectCount(incorrectCount)
                .accuracy(Math.round(accuracy * 100.0) / 100.0)
                .averageScore(Math.round(averageScore * 100.0) / 100.0)
                .build();
    }

    /**
     * 카테고리별 통계 조회
     */
    public List<CategoryStatsResponse> getCategoryStats(Long userId) {
        List<CategoryStatsResponse> stats = new ArrayList<>();

        for (Category category : Category.values()) {
            // 카테고리별 전체 문제 수
            long totalProblems = problemRepository.countByCategory(category);

            // 카테고리별 푼 문제들
            List<SolvedProblem> solvedProblems =
                    solvedProblemRepository.findByUserIdAndCategory(userId, category);

            long solvedCount = solvedProblems.size();
            long correctCount = solvedProblems.stream()
                    .filter(SolvedProblem::getIsCorrect)
                    .count();

            double accuracy = solvedCount > 0
                    ? (correctCount * 100.0) / solvedCount
                    : 0.0;

            // 난이도별 세부 통계
            Map<DifficultyType, ProgressResponse> difficultyStats = new HashMap<>();
            for (DifficultyType difficulty : DifficultyType.values()) {
                long totalByDifficulty = problemRepository.countByCategoryAndDifficulty(category, difficulty);

                List<SolvedProblem> solvedByDifficulty =
                        solvedProblemRepository.findByUserIdAndCategoryAndDifficulty(userId, category, difficulty);

                long solvedByDifficultyCount = solvedByDifficulty.size();
                long correctByDifficulty = solvedByDifficulty.stream()
                        .filter(SolvedProblem::getIsCorrect)
                        .count();

                difficultyStats.put(difficulty, ProgressResponse.of(
                        category,
                        difficulty,
                        totalByDifficulty,
                        solvedByDifficultyCount,
                        correctByDifficulty
                ));
            }

            stats.add(CategoryStatsResponse.builder()
                    .category(category)
                    .totalProblems(totalProblems)
                    .solvedProblems(solvedCount)
                    .correctCount(correctCount)
                    .accuracy(Math.round(accuracy * 100.0) / 100.0)
                    .difficultyStats(difficultyStats)
                    .build());
        }

        return stats;
    }

    /**
     * 특정 카테고리+난이도 통계 조회
     */
    public ProgressResponse getCategoryDifficultyStats(Long userId, Category category, DifficultyType difficulty) {
        long totalProblems = problemRepository.countByCategoryAndDifficulty(category, difficulty);

        List<SolvedProblem> solvedProblems =
                solvedProblemRepository.findByUserIdAndCategoryAndDifficulty(userId, category, difficulty);

        long solvedCount = solvedProblems.size();
        long correctCount = solvedProblems.stream()
                .filter(SolvedProblem::getIsCorrect)
                .count();

        return ProgressResponse.of(category, difficulty, totalProblems, solvedCount, correctCount);
    }

    /**
     * 취약점 분석 (정답률 낮은 순)
     */
    public List<WeakPointResponse> getWeakPoints(Long userId) {
        List<WeakPointResponse> weakPoints = new ArrayList<>();

        for (Category category : Category.values()) {
            for (DifficultyType difficulty : DifficultyType.values()) {
                List<SolvedProblem> solvedProblems =
                        solvedProblemRepository.findByUserIdAndCategoryAndDifficulty(userId, category, difficulty);

                if (solvedProblems.isEmpty()) {
                    continue; // 아직 안 푼 문제는 제외
                }

                long solvedCount = solvedProblems.size();
                long correctCount = solvedProblems.stream()
                        .filter(SolvedProblem::getIsCorrect)
                        .count();

                double accuracy = (correctCount * 100.0) / solvedCount;

                weakPoints.add(WeakPointResponse.builder()
                        .category(category)
                        .difficulty(difficulty)
                        .solvedCount(solvedCount)
                        .correctCount(correctCount)
                        .accuracy(Math.round(accuracy * 100.0) / 100.0)
                        .build());
            }
        }

        // 정답률 낮은 순으로 정렬
        return weakPoints.stream()
                .sorted(Comparator.comparingDouble(WeakPointResponse::getAccuracy))
                .collect(Collectors.toList());
    }

    /**
     * 오답 노트 (틀린 문제만)
     */
    public List<SolvedProblem> getWrongAnswers(Long userId) {
        return solvedProblemRepository.findByUserIdAndIsCorrect(userId, false);
    }
}