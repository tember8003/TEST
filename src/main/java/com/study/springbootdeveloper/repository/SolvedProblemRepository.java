package com.study.springbootdeveloper.repository;

import com.study.springbootdeveloper.domain.SolvedProblem;
import com.study.springbootdeveloper.type.Category;
import com.study.springbootdeveloper.type.DifficultyType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SolvedProblemRepository extends JpaRepository<SolvedProblem, Long> {

    // 특정 유저가 푼 모든 문제
    List<SolvedProblem> findByUserId(Long userId);

    // 특정 유저가 특정 문제를 풀었는지 확인
    boolean existsByUserIdAndProblemId(Long userId, Long problemId);

    // 특정 유저가 특정 문제를 푼 기록 조회
    Optional<SolvedProblem> findByUserIdAndProblemId(Long userId, Long problemId);

    // 특정 세션의 모든 풀이 기록
    List<SolvedProblem> findBySessionId(Long sessionId);

    // 특정 유저의 정답 개수
    long countByUserIdAndIsCorrect(Long userId, Boolean isCorrect);

    // 특정 유저가 특정 카테고리에서 푼 문제들
    @Query("SELECT sp FROM SolvedProblem sp JOIN sp.problem p WHERE sp.user.id = :userId AND p.category = :category")
    List<SolvedProblem> findByUserIdAndCategory(@Param("userId") Long userId, @Param("category") Category category);

    // 특정 유저가 특정 카테고리+난이도에서 푼 문제들
    @Query("SELECT sp FROM SolvedProblem sp JOIN sp.problem p WHERE sp.user.id = :userId AND p.category = :category AND p.difficulty = :difficulty")
    List<SolvedProblem> findByUserIdAndCategoryAndDifficulty(
            @Param("userId") Long userId,
            @Param("category") Category category,
            @Param("difficulty") DifficultyType difficulty
    );

    // 틀린 문제만 조회 (오답노트용)
    List<SolvedProblem> findByUserIdAndIsCorrect(Long userId, Boolean isCorrect);
}