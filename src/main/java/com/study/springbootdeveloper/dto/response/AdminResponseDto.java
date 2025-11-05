package com.study.springbootdeveloper.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

public class AdminResponseDto {

    /**
     * 문제 삭제 응답
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeleteProblemResponse {
        private String message;
        private Long problemId;
    }

    /**
     * 사용자 삭제 응답
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeleteUserResponse {
        private String message;
        private Long userId;
    }

    /**
     * 페이징된 문제 목록 응답
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaginatedProblemsResponse {
        private List<ProblemResponse> problems;
        private int currentPage;
        private int totalPages;
        private long totalElements;
        private int size;
        private boolean hasNext;
        private boolean hasPrevious;
    }

    /**
     * 문제 통계 응답
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProblemStatisticsResponse {
        private long totalCount;
        private Map<String, Long> byCategory;
        private Map<String, Long> byDifficulty;
        private Map<String, Map<String, Long>> byCategoryAndDifficulty;
    }

    /**
     * 사용자 통계 응답
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserStatisticsResponse {
        private long totalUsers;
        private long adminCount;
        private long userCount;
    }

    /**
     * 대시보드 통계 응답
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DashboardStatisticsResponse {
        private long totalUsers;
        private long totalProblems;
        private Map<String, Long> problemsByCategory;
        private Map<String, Long> problemsByDifficulty;
    }
}