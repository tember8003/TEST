package com.study.springbootdeveloper.service;

import com.study.springbootdeveloper.domain.Problem;
import com.study.springbootdeveloper.domain.Session;
import com.study.springbootdeveloper.domain.SolvedProblem;
import com.study.springbootdeveloper.domain.User;
import com.study.springbootdeveloper.handler.RestApiException;
import com.study.springbootdeveloper.repository.SessionRepository;
import com.study.springbootdeveloper.repository.SolvedProblemRepository;
import com.study.springbootdeveloper.repository.UserRepository;
import com.study.springbootdeveloper.type.Category;
import com.study.springbootdeveloper.type.DifficultyType;
import com.study.springbootdeveloper.type.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SessionService {

    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;
    private final ProblemService problemService;
    private final SolvedProblemRepository solvedProblemRepository;

    public SessionService(SessionRepository sessionRepository, UserRepository userRepository,
                          ProblemService problemService, SolvedProblemRepository solvedProblemRepository) {
        this.sessionRepository = sessionRepository;
        this.userRepository = userRepository;
        this.problemService = problemService;
        this.solvedProblemRepository = solvedProblemRepository;
    }

    /**
     * 새로운 챌린지 세션 생성
     */
    public Session createSession(Long userId, Category category, DifficultyType difficulty, Integer totalQuestions, Integer timeLimitMinutes) {
        User user = userId != null ? userRepository.findById(userId).orElse(null) : null;

        // 기본값 설정
        if (totalQuestions == null || totalQuestions <= 0) {
            totalQuestions = 10;
        }

        // 해당 카테고리/난이도의 문제가 충분한지 확인
        long availableProblems = problemService.countByCategoryAndDifficulty(category, difficulty);
        if (availableProblems < totalQuestions) {
            throw new RestApiException(ErrorCode.INSUFFICIENT_PROBLEMS,
                    String.format("문제가 부족합니다. 요청: %d개, 사용가능: %d개", totalQuestions, availableProblems));
        }

        Session session = Session.builder()
                .user(user)
                .category(category)
                .difficulty(difficulty)
                .totalQuestions(totalQuestions)
                .correctCount(0)
                .timeLimitMinutes(timeLimitMinutes)
                .build();

        return sessionRepository.save(session);
    }

    /**
     * 세션 조회
     */
    @Transactional(readOnly = true)
    public Session getSession(Long sessionId) {
        return sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RestApiException(ErrorCode.SESSION_NOT_FOUND));
    }

    /**
     * 세션에 포함된 문제들 조회
     */
    @Transactional(readOnly = true)
    public List<Problem> getSessionProblems(Long sessionId) {
        Session session = getSession(sessionId);
        return problemService.getRandomProblems(
                session.getCategory(),
                session.getDifficulty(),
                session.getTotalQuestions()
        );
    }

    /**
     * 세션 완료 처리
     */
    public void completeSession(Long sessionId) {
        Session session = getSession(sessionId);

        if (session.getCompletedAt() != null) {
            throw new RestApiException(ErrorCode.SESSION_ALREADY_COMPLETED);
        }

        session.complete();
        sessionRepository.save(session);
    }

    /**
     * 세션의 풀이 기록 조회
     */
    @Transactional(readOnly = true)
    public List<SolvedProblem> getSessionSolvedProblems(Long sessionId) {
        return solvedProblemRepository.findBySessionId(sessionId);
    }

    /**
     * 특정 유저의 모든 세션 조회
     */
    @Transactional(readOnly = true)
    public List<Session> getUserSessions(Long userId) {
        return sessionRepository.findByUserId(userId);
    }

    /**
     * 특정 유저의 완료된 세션만 조회
     */
    @Transactional(readOnly = true)
    public List<Session> getUserCompletedSessions(Long userId) {
        return sessionRepository.findByUserIdAndCompletedAtIsNotNull(userId);
    }

    /**
     * 특정 유저의 진행중인 세션 조회
     */
    @Transactional(readOnly = true)
    public Session getUserActiveSession(Long userId) {
        return sessionRepository.findFirstByUserIdAndCompletedAtIsNullOrderByStartedAtDesc(userId)
                .orElse(null);
    }

    /**
     * 세션 삭제 (테스트용 또는 관리자용)
     */
    public void deleteSession(Long sessionId) {
        sessionRepository.deleteById(sessionId);
    }
}