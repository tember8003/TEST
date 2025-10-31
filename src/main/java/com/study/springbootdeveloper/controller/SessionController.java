package com.study.springbootdeveloper.controller;

import com.study.springbootdeveloper.domain.Problem;
import com.study.springbootdeveloper.domain.Session;
import com.study.springbootdeveloper.domain.SolvedProblem;
import com.study.springbootdeveloper.dto.request.CreateSessionRequest;
import com.study.springbootdeveloper.dto.request.SubmitAnswerRequest;
import com.study.springbootdeveloper.dto.response.ProblemResponse;
import com.study.springbootdeveloper.dto.response.SessionResponse;
import com.study.springbootdeveloper.dto.response.SolvedProblemResponse;
import com.study.springbootdeveloper.service.SessionService;
import com.study.springbootdeveloper.service.SolvingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
public class SessionController {

    private final SessionService sessionService;
    private final SolvingService solvingService;

    /**
     * 챌린지 세션 생성
     */
    @PostMapping
    public ResponseEntity<SessionResponse> createSession(
            @RequestParam(required = false) Long userId,
            @Valid @RequestBody CreateSessionRequest request
    ) {
        Session session = sessionService.createSession(
                userId,
                request.getCategory(),
                request.getDifficulty(),
                request.getTotalQuestions(),
                request.getTimeLimitMinutes()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SessionResponse.from(session));
    }

    /**
     * 세션 정보 조회
     */
    @GetMapping("/{sessionId}")
    public ResponseEntity<SessionResponse> getSession(@PathVariable Long sessionId) {
        Session session = sessionService.getSession(sessionId);
        return ResponseEntity.ok(SessionResponse.from(session));
    }

    /**
     * 세션의 문제들 조회
     */
    @GetMapping("/{sessionId}/problems")
    public ResponseEntity<List<ProblemResponse>> getSessionProblems(@PathVariable Long sessionId) {
        List<Problem> problems = sessionService.getSessionProblems(sessionId);

        List<ProblemResponse> responses = problems.stream()
                .map(ProblemResponse::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    /**
     * 세션 내 답안 제출
     */
    @PostMapping("/{sessionId}/submit")
    public ResponseEntity<SolvedProblemResponse> submitAnswerInSession(
            @PathVariable Long sessionId,
            @Valid @RequestBody SubmitAnswerRequest request
    ) {
        SolvedProblem solvedProblem = solvingService.submitAnswerInSession(
                sessionId,
                request.getProblemId(),
                request.getUserAnswer()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SolvedProblemResponse.from(solvedProblem));
    }

    /**
     * 세션 완료
     */
    @PostMapping("/{sessionId}/complete")
    public ResponseEntity<SessionResponse> completeSession(@PathVariable Long sessionId) {
        sessionService.completeSession(sessionId);
        Session session = sessionService.getSession(sessionId);
        return ResponseEntity.ok(SessionResponse.from(session));
    }

    /**
     * 세션 결과 조회
     */
    @GetMapping("/{sessionId}/results")
    public ResponseEntity<List<SolvedProblemResponse>> getSessionResults(@PathVariable Long sessionId) {
        List<SolvedProblem> solvedProblems = sessionService.getSessionSolvedProblems(sessionId);

        List<SolvedProblemResponse> responses = solvedProblems.stream()
                .map(SolvedProblemResponse::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    /**
     * 유저의 세션 목록 조회
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<SessionResponse>> getUserSessions(@PathVariable Long userId) {
        List<Session> sessions = sessionService.getUserSessions(userId);

        List<SessionResponse> responses = sessions.stream()
                .map(SessionResponse::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    /**
     * 유저의 완료된 세션 조회
     */
    @GetMapping("/user/{userId}/completed")
    public ResponseEntity<List<SessionResponse>> getUserCompletedSessions(@PathVariable Long userId) {
        List<Session> sessions = sessionService.getUserCompletedSessions(userId);

        List<SessionResponse> responses = sessions.stream()
                .map(SessionResponse::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    /**
     * 유저의 진행중인 세션 조회
     */
    @GetMapping("/user/{userId}/active")
    public ResponseEntity<SessionResponse> getUserActiveSession(@PathVariable Long userId) {
        Session session = sessionService.getUserActiveSession(userId);

        if (session == null) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(SessionResponse.from(session));
    }
}