package com.study.springbootdeveloper.controller;

import com.study.springbootdeveloper.domain.Problem;
import com.study.springbootdeveloper.domain.User;
import com.study.springbootdeveloper.dto.request.CreateProblemRequest;
import com.study.springbootdeveloper.dto.request.UpdateProblemRequest;
import com.study.springbootdeveloper.dto.response.AdminResponseDto;
import com.study.springbootdeveloper.dto.response.ProblemResponse;
import com.study.springbootdeveloper.dto.response.UserResponse;
import com.study.springbootdeveloper.repository.UserRepository;
import com.study.springbootdeveloper.service.ProblemService;
import com.study.springbootdeveloper.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Tag(name = "Admin", description = "관리자 전용 API")
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final ProblemService problemService;
    private final UserService userService;
    private final UserRepository userRepository;

    // ==================== 문제 관리 ====================

    /**
     * 문제 생성
     */
    @Operation(summary = "문제 생성", description = "새로운 문제를 생성합니다. (관리자 전용)")
    @PostMapping("/problems")
    public ResponseEntity<ProblemResponse> createProblem(@Valid @RequestBody CreateProblemRequest request) {
        log.info("Admin creating problem: category={}, difficulty={}", request.getCategory(), request.getDifficulty());

        Problem problem = problemService.createProblem(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ProblemResponse.from(problem));
    }

    /**
     * 문제 수정
     */
    @Operation(summary = "문제 수정", description = "기존 문제를 수정합니다. (관리자 전용)")
    @PutMapping("/problems/{problemId}")
    public ResponseEntity<ProblemResponse> updateProblem(
            @PathVariable Long problemId,
            @Valid @RequestBody UpdateProblemRequest request) {
        log.info("Admin updating problem: id={}", problemId);

        Problem problem = problemService.updateProblem(problemId, request);

        return ResponseEntity.ok(ProblemResponse.from(problem));
    }

    /**
     * 문제 삭제
     */
    @Operation(summary = "문제 삭제", description = "문제를 삭제합니다. (관리자 전용)")
    @DeleteMapping("/problems/{problemId}")
    public ResponseEntity<AdminResponseDto.DeleteProblemResponse> deleteProblem(@PathVariable Long problemId) {
        log.info("Admin deleting problem: id={}", problemId);

        problemService.deleteProblem(problemId);

        AdminResponseDto.DeleteProblemResponse response = AdminResponseDto.DeleteProblemResponse.builder()
                .message("문제가 삭제되었습니다.")
                .problemId(problemId)
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * 전체 문제 조회 (페이징)
     */
    @Operation(summary = "전체 문제 조회", description = "페이징된 전체 문제 목록을 조회합니다. (관리자 전용)")
    @GetMapping("/problems")
    public ResponseEntity<AdminResponseDto.PaginatedProblemsResponse> getAllProblemsWithPaging(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        Page<Problem> problemPage = problemService.getAllProblemsWithPaging(pageable);

        List<ProblemResponse> problems = problemPage.getContent().stream()
                .map(ProblemResponse::from)
                .collect(Collectors.toList());

        AdminResponseDto.PaginatedProblemsResponse response = AdminResponseDto.PaginatedProblemsResponse.builder()
                .problems(problems)
                .currentPage(problemPage.getNumber())
                .totalPages(problemPage.getTotalPages())
                .totalElements(problemPage.getTotalElements())
                .size(problemPage.getSize())
                .hasNext(problemPage.hasNext())
                .hasPrevious(problemPage.hasPrevious())
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * 문제 통계
     */
    @Operation(summary = "문제 통계", description = "카테고리별, 난이도별 문제 통계를 조회합니다. (관리자 전용)")
    @GetMapping("/problems/statistics")
    public ResponseEntity<AdminResponseDto.ProblemStatisticsResponse> getProblemStatistics() {
        Map<String, Object> statistics = problemService.getProblemStatistics();

        AdminResponseDto.ProblemStatisticsResponse response = AdminResponseDto.ProblemStatisticsResponse.builder()
                .totalCount((Long) statistics.get("totalCount"))
                .byCategory((Map<String, Long>) statistics.get("byCategory"))
                .byDifficulty((Map<String, Long>) statistics.get("byDifficulty"))
                .byCategoryAndDifficulty((Map<String, Map<String, Long>>) statistics.get("byCategoryAndDifficulty"))
                .build();

        return ResponseEntity.ok(response);
    }

    // ==================== 사용자 관리 ====================

    /**
     * 전체 사용자 조회
     */
    @Operation(summary = "전체 사용자 조회", description = "모든 사용자 목록을 조회합니다. (관리자 전용)")
    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        log.info("Admin fetching all users");

        List<User> users = userRepository.findAll();

        List<UserResponse> responses = users.stream()
                .map(user -> UserResponse.builder()
                        .userId(user.getId())
                        .loginId(user.getLoginId())
                        .nickname(user.getNickname())
                        .createdAt(user.getCreatedAt())
                        .build())
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    /**
     * 특정 사용자 조회
     */
    @Operation(summary = "사용자 조회", description = "특정 사용자의 상세 정보를 조회합니다. (관리자 전용)")
    @GetMapping("/users/{userId}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long userId) {
        log.info("Admin fetching user: id={}", userId);

        UserResponse userResponse = userService.getUserById(userId);

        return ResponseEntity.ok(userResponse);
    }

    /**
     * 사용자 삭제
     */
    @Operation(summary = "사용자 삭제", description = "사용자를 삭제합니다. (관리자 전용)")
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<AdminResponseDto.DeleteUserResponse> deleteUser(@PathVariable Long userId) {
        log.info("Admin deleting user: id={}", userId);

        userService.deleteUser(userId);

        AdminResponseDto.DeleteUserResponse response = AdminResponseDto.DeleteUserResponse.builder()
                .message("사용자가 삭제되었습니다.")
                .userId(userId)
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * 사용자 통계
     */
    @Operation(summary = "사용자 통계", description = "전체 사용자 통계를 조회합니다. (관리자 전용)")
    @GetMapping("/users/statistics")
    public ResponseEntity<AdminResponseDto.UserStatisticsResponse> getUserStatistics() {
        log.info("Admin fetching user statistics");

        long totalUsers = userRepository.count();
        long adminCount = userRepository.countByRole(com.study.springbootdeveloper.type.UserRole.ROLE_ADMIN);
        long userCount = userRepository.countByRole(com.study.springbootdeveloper.type.UserRole.ROLE_USER);

        AdminResponseDto.UserStatisticsResponse response = AdminResponseDto.UserStatisticsResponse.builder()
                .totalUsers(totalUsers)
                .adminCount(adminCount)
                .userCount(userCount)
                .build();

        return ResponseEntity.ok(response);
    }

    // ==================== 대시보드 ====================

    /**
     * 관리자 대시보드 전체 통계
     */
    @Operation(summary = "대시보드 통계", description = "관리자 대시보드에 표시할 전체 통계를 조회합니다. (관리자 전용)")
    @GetMapping("/dashboard")
    public ResponseEntity<AdminResponseDto.DashboardStatisticsResponse> getDashboardStatistics() {
        log.info("Admin fetching dashboard statistics");

        // 사용자 통계
        long totalUsers = userRepository.count();

        // 문제 통계
        Map<String, Object> problemStats = problemService.getProblemStatistics();

        AdminResponseDto.DashboardStatisticsResponse response = AdminResponseDto.DashboardStatisticsResponse.builder()
                .totalUsers(totalUsers)
                .totalProblems((Long) problemStats.get("totalCount"))
                .problemsByCategory((Map<String, Long>) problemStats.get("byCategory"))
                .problemsByDifficulty((Map<String, Long>) problemStats.get("byDifficulty"))
                .build();

        return ResponseEntity.ok(response);
    }
}