package com.study.springbootdeveloper.controller;

import com.study.springbootdeveloper.domain.Problem;
import com.study.springbootdeveloper.domain.SolvedProblem;
import com.study.springbootdeveloper.dto.request.SubmitAnswerRequest;
import com.study.springbootdeveloper.dto.response.ProblemResponse;
import com.study.springbootdeveloper.dto.response.SolvedProblemResponse;
import com.study.springbootdeveloper.service.ProblemService;
import com.study.springbootdeveloper.service.SolvingService;
import com.study.springbootdeveloper.type.Category;
import com.study.springbootdeveloper.type.DifficultyType;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/problems")
@RequiredArgsConstructor
public class ProblemController {

    private final ProblemService problemService;
    private final SolvingService solvingService;

    private static final String GUEST_ID_COOKIE_NAME = "guest_id";
    private static final int COOKIE_MAX_AGE = 60 * 60 * 24 * 30; // 30일

    /**
     * 모든 문제 조회
     */
    @GetMapping
    public ResponseEntity<List<ProblemResponse>> getAllProblems(
            @RequestParam(required = false) Category category,
            @RequestParam(required = false) DifficultyType difficulty,
            @RequestParam(required = false) Long userId
    ) {
        List<Problem> problems;

        if (category != null && difficulty != null) {
            problems = problemService.getProblemsByCategoryAndDifficulty(category, difficulty);
        } else if (category != null) {
            problems = problemService.getProblemsByCategory(category);
        } else if (difficulty != null) {
            problems = problemService.getProblemsByDifficulty(difficulty);
        } else {
            problems = problemService.getAllProblems();
        }

        List<ProblemResponse> responses = problems.stream()
                .map(problem -> {
                    if (userId != null) {
                        boolean isSolved = problemService.isSolvedByUser(userId, problem.getId());
                        return ProblemResponse.from(problem, isSolved);
                    }
                    return ProblemResponse.from(problem);
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    /*
     특정 문제 상세 조회
     */
    @GetMapping("/{problemId}")
    public ResponseEntity<ProblemResponse> getProblem(
            @PathVariable Long problemId,
            @RequestParam(required = false) Long userId
    ) {
        Problem problem = problemService.getProblemById(problemId);

        ProblemResponse response;
        if (userId != null) {
            boolean isSolved = problemService.isSolvedByUser(userId, problemId);
            response = ProblemResponse.from(problem, isSolved);
        } else {
            response = ProblemResponse.from(problem);
        }

        return ResponseEntity.ok(response);
    }

    /*
     답안 제출 (자유 선택 모드)
     비로그인 사용자도 제출 가능 (쿠키 기반 UUID 추적)
     */
    @PostMapping("/submit")
    public ResponseEntity<SolvedProblemResponse> submitAnswer(
            @RequestParam(required = false) Long userId,
            @Valid @RequestBody SubmitAnswerRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse
    ) {
        // 비로그인 사용자인 경우 guestId 처리
        String guestId = null;
        if (userId == null) {
            guestId = getOrCreateGuestId(httpRequest, httpResponse);
            log.info("Guest user submission with guestId: {}", guestId);
        }

        SolvedProblem solvedProblem = solvingService.submitAnswer(
                userId,
                request.getProblemId(),
                request.getUserAnswer(),
                guestId
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SolvedProblemResponse.from(solvedProblem));
    }

    /*
      풀이 기록 조회 (로그인 사용자만)
     */
    @GetMapping("/solved/{problemId}")
    public ResponseEntity<SolvedProblemResponse> getSolvedProblem(
            @PathVariable Long problemId,
            @RequestParam Long userId
    ) {
        SolvedProblem solvedProblem = solvingService.getSolvedProblem(userId, problemId);
        return ResponseEntity.ok(SolvedProblemResponse.from(solvedProblem));
    }

    /**
     * 쿠키에서 guestId 추출 또는 새로 생성
     *
     * @param request HTTP 요청
     * @param response HTTP 응답 (쿠키 설정용)
     * @return guestId (UUID)
     */
    private String getOrCreateGuestId(HttpServletRequest request, HttpServletResponse response) {
        // 쿠키에서 기존 guestId 확인
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (GUEST_ID_COOKIE_NAME.equals(cookie.getName())) {
                    String existingGuestId = cookie.getValue();
                    log.debug("Found existing guestId in cookie: {}", existingGuestId);
                    return existingGuestId;
                }
            }
        }

        // 쿠키 없으면 새 UUID 생성
        String newGuestId = UUID.randomUUID().toString();
        log.info("Creating new guestId: {}", newGuestId);

        // 쿠키 설정
        Cookie guestIdCookie = new Cookie(GUEST_ID_COOKIE_NAME, newGuestId);
        guestIdCookie.setHttpOnly(true);  // XSS 방지
        guestIdCookie.setSecure(false);    // 개발 환경에서는 false (운영에서는 true)
        guestIdCookie.setPath("/");
        guestIdCookie.setMaxAge(COOKIE_MAX_AGE);  // 30일
        response.addCookie(guestIdCookie);

        return newGuestId;
    }
}