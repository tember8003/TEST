package com.study.springbootdeveloper.dto.response;

import com.study.springbootdeveloper.domain.SolvedProblem;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class UserResponse {

    private Long userId;
    private String loginId;
    private String nickname;
    private List<SolvedProblem> solvedProblems;
    private LocalDateTime createdAt;
}
