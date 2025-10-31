package com.study.springbootdeveloper.repository;

import com.study.springbootdeveloper.domain.Session;
import com.study.springbootdeveloper.type.Category;
import com.study.springbootdeveloper.type.DifficultyType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {

    // 특정 유저의 모든 세션
    List<Session> findByUserId(Long userId);

    // 특정 유저의 완료된 세션만
    List<Session> findByUserIdAndCompletedAtIsNotNull(Long userId);

    // 특정 유저의 진행중인 세션 (completedAt이 null)
    List<Session> findByUserIdAndCompletedAtIsNull(Long userId);

    // 특정 유저의 특정 카테고리 세션
    List<Session> findByUserIdAndCategory(Long userId, Category category);

    // 특정 유저의 특정 카테고리+난이도 세션
    List<Session> findByUserIdAndCategoryAndDifficulty(Long userId, Category category, DifficultyType difficulty);

    // 진행중인 세션 조회 (단일)
    Optional<Session> findFirstByUserIdAndCompletedAtIsNullOrderByStartedAtDesc(Long userId);
}