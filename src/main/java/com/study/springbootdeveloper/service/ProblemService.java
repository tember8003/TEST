package com.study.springbootdeveloper.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.springbootdeveloper.domain.Problem;
import com.study.springbootdeveloper.dto.request.CreateProblemRequest;
import com.study.springbootdeveloper.dto.request.UpdateProblemRequest;
import com.study.springbootdeveloper.handler.RestApiException;
import com.study.springbootdeveloper.repository.ProblemRepository;
import com.study.springbootdeveloper.repository.SolvedProblemRepository;
import com.study.springbootdeveloper.type.Category;
import com.study.springbootdeveloper.type.DifficultyType;
import com.study.springbootdeveloper.type.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@Transactional(readOnly = true)
public class ProblemService {

    private final ProblemRepository problemRepository;
    private final SolvedProblemRepository solvedProblemRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ProblemService(ProblemRepository problemRepository, SolvedProblemRepository solvedProblemRepository) {
        this.problemRepository = problemRepository;
        this.solvedProblemRepository = solvedProblemRepository;
    }

    // ==================== 기존 메서드들 ====================

    public List<Problem> getAllProblems() {
        return problemRepository.findAll();
    }

    public Problem getProblemById(Long problemId) {
        return problemRepository.findById(problemId)
                .orElseThrow(() -> new RestApiException(ErrorCode.PROBLEM_NOT_FOUND));
    }

    public List<Problem> getProblemsByCategory(Category category) {
        return problemRepository.findByCategory(category);
    }

    public List<Problem> getProblemsByDifficulty(DifficultyType difficulty) {
        return problemRepository.findByDifficulty(difficulty);
    }

    public List<Problem> getProblemsByCategoryAndDifficulty(Category category, DifficultyType difficulty) {
        return problemRepository.findByCategoryAndDifficulty(category, difficulty);
    }

    public boolean isSolvedByUser(Long userId, Long problemId) {
        if (userId == null) {
            return false;
        }
        return solvedProblemRepository.existsByUserIdAndProblemId(userId, problemId);
    }

    public long countByCategory(Category category) {
        return problemRepository.countByCategory(category);
    }

    public long countByCategoryAndDifficulty(Category category, DifficultyType difficulty) {
        return problemRepository.countByCategoryAndDifficulty(category, difficulty);
    }

    public List<Problem> getRandomProblems(Category category, DifficultyType difficulty, int count) {
        return problemRepository.findRandomProblems(
                category.name(),
                difficulty.name(),
                count
        );
    }

    // ==================== 관리자 기능 ====================

    /**
     * 문제 생성 (관리자 전용)
     */
    @Transactional
    public Problem createProblem(CreateProblemRequest request) {
        log.info("Creating new problem: category={}, difficulty={}", request.getCategory(), request.getDifficulty());

        // choices를 JSON으로 변환
        String choicesJson = null;
        if (request.getChoices() != null && !request.getChoices().isEmpty()) {
            try {
                choicesJson = objectMapper.writeValueAsString(request.getChoices());
            } catch (Exception e) {
                log.error("Failed to convert choices to JSON", e);
                throw new RestApiException(ErrorCode.INTERNAL_SERVER_ERROR);
            }
        }

        Problem problem = Problem.builder()
                .problemType(request.getProblemType())
                .category(request.getCategory())
                .difficulty(request.getDifficulty())
                .question(request.getQuestion())
                .choicesJson(choicesJson)
                .answer(request.getAnswer())
                .explanation(request.getExplanation())
                .build();

        Problem saved = problemRepository.save(problem);
        log.info("Problem created successfully: id={}", saved.getId());
        return saved;
    }

    /**
     * 문제 수정 (관리자 전용)
     */
    @Transactional
    public Problem updateProblem(Long problemId, UpdateProblemRequest request) {
        log.info("Updating problem: id={}", problemId);

        Problem problem = getProblemById(problemId);

        // Null이 아닌 필드만 업데이트 (Partial Update)
        if (request.getProblemType() != null) {
            problem.updateProblemType(request.getProblemType());
        }
        if (request.getCategory() != null) {
            problem.updateCategory(request.getCategory());
        }
        if (request.getDifficulty() != null) {
            problem.updateDifficulty(request.getDifficulty());
        }
        if (request.getQuestion() != null) {
            problem.updateQuestion(request.getQuestion());
        }
        if (request.getChoices() != null) {
            try {
                String choicesJson = objectMapper.writeValueAsString(request.getChoices());
                problem.updateChoices(choicesJson);
            } catch (Exception e) {
                log.error("Failed to convert choices to JSON", e);
                throw new RestApiException(ErrorCode.INTERNAL_SERVER_ERROR);
            }
        }
        if (request.getAnswer() != null) {
            problem.updateAnswer(request.getAnswer());
        }
        if (request.getExplanation() != null) {
            problem.updateExplanation(request.getExplanation());
        }

        Problem updated = problemRepository.save(problem);
        log.info("Problem updated successfully: id={}", updated.getId());
        return updated;
    }

    /**
     * 문제 삭제 (관리자 전용)
     */
    @Transactional
    public void deleteProblem(Long problemId) {
        log.info("Deleting problem: id={}", problemId);

        Problem problem = getProblemById(problemId);
        problemRepository.delete(problem);

        log.info("Problem deleted successfully: id={}", problemId);
    }

    /**
     * 페이징된 전체 문제 조회 (관리자 전용)
     */
    public Page<Problem> getAllProblemsWithPaging(Pageable pageable) {
        return problemRepository.findAll(pageable);
    }

    /**
     * 문제 통계 조회 (관리자 전용)
     */
    public Map<String, Object> getProblemStatistics() {
        Map<String, Object> statistics = new HashMap<>();

        // 전체 문제 수
        long totalCount = problemRepository.count();
        statistics.put("totalCount", totalCount);

        // 카테고리별 개수
        Map<String, Long> categoryStats = new HashMap<>();
        for (Category category : Category.values()) {
            categoryStats.put(category.name(), problemRepository.countByCategory(category));
        }
        statistics.put("byCategory", categoryStats);

        // 난이도별 개수
        Map<String, Long> difficultyStats = new HashMap<>();
        for (DifficultyType difficulty : DifficultyType.values()) {
            long count = problemRepository.findByDifficulty(difficulty).size();
            difficultyStats.put(difficulty.name(), count);
        }
        statistics.put("byDifficulty", difficultyStats);

        // 카테고리 + 난이도 조합별 개수
        Map<String, Map<String, Long>> categoryDifficultyStats = new HashMap<>();
        for (Category category : Category.values()) {
            Map<String, Long> diffStats = new HashMap<>();
            for (DifficultyType difficulty : DifficultyType.values()) {
                long count = problemRepository.countByCategoryAndDifficulty(category, difficulty);
                diffStats.put(difficulty.name(), count);
            }
            categoryDifficultyStats.put(category.name(), diffStats);
        }
        statistics.put("byCategoryAndDifficulty", categoryDifficultyStats);

        return statistics;
    }

    /**
     * 여러 문제 일괄 저장 (초기화용)
     */
    @Transactional
    public List<Problem> saveAllProblems(List<Problem> problems) {
        return problemRepository.saveAll(problems);
    }
}