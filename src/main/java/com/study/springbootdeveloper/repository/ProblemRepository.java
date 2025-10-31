package com.study.springbootdeveloper.repository;

import com.study.springbootdeveloper.domain.Problem;
import com.study.springbootdeveloper.type.Category;
import com.study.springbootdeveloper.type.DifficultyType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProblemRepository extends JpaRepository<Problem, Long> {

    // 카테고리별 조회
    List<Problem> findByCategory(Category category);

    // 난이도별 조회
    List<Problem> findByDifficulty(DifficultyType difficulty);

    // 카테고리 + 난이도 조회
    List<Problem> findByCategoryAndDifficulty(Category category, DifficultyType difficulty);

    // 카테고리별 문제 개수
    long countByCategory(Category category);

    // 카테고리 + 난이도별 문제 개수
    long countByCategoryAndDifficulty(Category category, DifficultyType difficulty);

    // 챌린지 모드용: 랜덤으로 N개 문제 가져오기
    @Query(value = "SELECT * FROM problems WHERE category = :category AND difficulty = :difficulty ORDER BY RAND() LIMIT :limit", nativeQuery = true)
    List<Problem> findRandomProblems(
            @Param("category") String category,
            @Param("difficulty") String difficulty,
            @Param("limit") int limit
    );
}