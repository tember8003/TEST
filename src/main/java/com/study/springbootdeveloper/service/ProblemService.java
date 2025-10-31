package com.study.springbootdeveloper.service;

import com.study.springbootdeveloper.domain.Problem;
import com.study.springbootdeveloper.handler.RestApiException;
import com.study.springbootdeveloper.repository.ProblemRepository;
import com.study.springbootdeveloper.repository.SolvedProblemRepository;
import com.study.springbootdeveloper.type.Category;
import com.study.springbootdeveloper.type.DifficultyType;
import com.study.springbootdeveloper.type.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ProblemService {

    private final ProblemRepository problemRepository;
    private final SolvedProblemRepository solvedProblemRepository;

    public ProblemService(ProblemRepository problemRepository, SolvedProblemRepository solvedProblemRepository) {
        this.problemRepository = problemRepository;
        this.solvedProblemRepository = solvedProblemRepository;
    }

    /**
     * 모든 문제 조회
     */
    public List<Problem> getAllProblems() {
        return problemRepository.findAll();
    }

    /**
     * 문제 ID로 조회
     */
    public Problem getProblemById(Long problemId) {
        return problemRepository.findById(problemId)
                .orElseThrow(() -> new RestApiException(ErrorCode.PROBLEM_NOT_FOUND));
    }

    /**
     * 카테고리별 문제 조회
     */
    public List<Problem> getProblemsByCategory(Category category) {
        return problemRepository.findByCategory(category);
    }

    /**
     * 난이도별 문제 조회
     */
    public List<Problem> getProblemsByDifficulty(DifficultyType difficulty) {
        return problemRepository.findByDifficulty(difficulty);
    }

    /**
     * 카테고리 + 난이도로 문제 조회 (자유 선택 모드용)
     */
    public List<Problem> getProblemsByCategoryAndDifficulty(Category category, DifficultyType difficulty) {
        return problemRepository.findByCategoryAndDifficulty(category, difficulty);
    }

    /**
     * 특정 유저가 이미 푼 문제인지 확인
     */
    public boolean isSolvedByUser(Long userId, Long problemId) {
        if (userId == null) {
            return false;
        }
        return solvedProblemRepository.existsByUserIdAndProblemId(userId, problemId);
    }

    /**
     * 카테고리별 문제 개수
     */
    public long countByCategory(Category category) {
        return problemRepository.countByCategory(category);
    }

    /**
     * 카테고리 + 난이도별 문제 개수
     */
    public long countByCategoryAndDifficulty(Category category, DifficultyType difficulty) {
        return problemRepository.countByCategoryAndDifficulty(category, difficulty);
    }

    /**
     * 랜덤으로 N개 문제 가져오기 (챌린지 모드용)
     */
    public List<Problem> getRandomProblems(Category category, DifficultyType difficulty, int count) {
        return problemRepository.findRandomProblems(
                category.name(),
                difficulty.name(),
                count
        );
    }

    /**
     * 문제 저장 (초기 데이터 로딩용)
     */
    @Transactional
    public Problem saveProblem(Problem problem) {
        return problemRepository.save(problem);
    }

    /**
     * 여러 문제 일괄 저장
     */
    @Transactional
    public List<Problem> saveAllProblems(List<Problem> problems) {
        return problemRepository.saveAll(problems);
    }
}