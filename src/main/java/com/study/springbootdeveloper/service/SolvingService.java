package com.study.springbootdeveloper.service;

import com.study.springbootdeveloper.domain.Problem;
import com.study.springbootdeveloper.domain.Session;
import com.study.springbootdeveloper.domain.SolvedProblem;
import com.study.springbootdeveloper.domain.User;
import com.study.springbootdeveloper.handler.RestApiException;
import com.study.springbootdeveloper.repository.ProblemRepository;
import com.study.springbootdeveloper.repository.SessionRepository;
import com.study.springbootdeveloper.repository.SolvedProblemRepository;
import com.study.springbootdeveloper.repository.UserRepository;
import com.study.springbootdeveloper.type.ErrorCode;
import com.study.springbootdeveloper.type.ProblemType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class SolvingService {

    private final SolvedProblemRepository solvedProblemRepository;
    private final ProblemRepository problemRepository;
    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final GeminiApiService geminiApiService;

    public SolvingService(SolvedProblemRepository solvedProblemRepository, ProblemRepository problemRepository,
                          UserRepository userRepository, SessionRepository sessionRepository,
                          GeminiApiService geminiApiService) {
        this.solvedProblemRepository = solvedProblemRepository;
        this.problemRepository = problemRepository;
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.geminiApiService = geminiApiService;
    }

    /**
     * 답안 제출 및 채점 (자유 선택 모드)
     */
    public SolvedProblem submitAnswer(Long userId, Long problemId, String userAnswer) {
        User user = userId != null ? userRepository.findById(userId).orElse(null) : null;
        Problem problem = problemRepository.findById(problemId)
                .orElseThrow(() -> new RestApiException(ErrorCode.PROBLEM_NOT_FOUND));

        // 이미 푼 문제인지 확인
        if (userId != null && solvedProblemRepository.existsByUserIdAndProblemId(userId, problemId)) {
            throw new RestApiException(ErrorCode.PROBLEM_ALREADY_SOLVED);
        }

        return processAnswer(user, problem, null, userAnswer);
    }

    /**
     * 세션 내 답안 제출 및 채점 (챌린지 모드)
     */
    public SolvedProblem submitAnswerInSession(Long sessionId, Long problemId, String userAnswer) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RestApiException(ErrorCode.SESSION_NOT_FOUND));

        Problem problem = problemRepository.findById(problemId)
                .orElseThrow(() -> new RestApiException(ErrorCode.PROBLEM_NOT_FOUND));

        // 세션 내에서 이미 푼 문제인지 확인
        boolean alreadySolved = solvedProblemRepository.findBySessionId(sessionId).stream()
                .anyMatch(sp -> sp.getProblem().getId().equals(problemId));

        if (alreadySolved) {
            throw new RestApiException(ErrorCode.PROBLEM_ALREADY_SOLVED);
        }

        SolvedProblem solvedProblem = processAnswer(session.getUser(), problem, session, userAnswer);

        // 정답이면 세션의 correctCount 증가
        if (solvedProblem.getIsCorrect()) {
            session.incrementCorrectCount();
        }

        return solvedProblem;
    }

    /**
     * 답안 처리 핵심 로직
     */
    private SolvedProblem processAnswer(User user, Problem problem, Session session, String userAnswer) {
        boolean isCorrect;
        int score;
        String aiFeedback;

        if (problem.getProblemType() == ProblemType.MULTIPLE_CHOICE) {
            // 객관식: 정확히 일치하는지 확인
            isCorrect = problem.getAnswer().trim().equalsIgnoreCase(userAnswer.trim());
            score = isCorrect ? 100 : 0;

            // Gemini API로 보충 설명 생성
            aiFeedback = geminiApiService.generateExplanation(problem, userAnswer, isCorrect);

        } else {
            // 단답형/서술형: Gemini API로 채점
            var gradingResult = geminiApiService.gradeAnswer(problem, userAnswer);
            isCorrect = gradingResult.isCorrect();
            score = gradingResult.getScore();
            aiFeedback = gradingResult.getFeedback();
        }

        // SolvedProblem 저장
        SolvedProblem solvedProblem = SolvedProblem.builder()
                .user(user)
                .problem(problem)
                .session(session)
                .userAnswer(userAnswer)
                .isCorrect(isCorrect)
                .score(score)
                .aiFeedback(aiFeedback)
                .build();

        return solvedProblemRepository.save(solvedProblem);
    }

    /**
     * 특정 유저의 풀이 기록 조회
     */
    @Transactional(readOnly = true)
    public SolvedProblem getSolvedProblem(Long userId, Long problemId) {
        return solvedProblemRepository.findByUserIdAndProblemId(userId, problemId)
                .orElseThrow(() -> new IllegalArgumentException("풀이 기록을 찾을 수 없습니다."));
    }
}