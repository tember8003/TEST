package com.study.springbootdeveloper.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class SolvedProblem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id", nullable = false)
    private Problem problem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id")
    private Session session;

    @Column(columnDefinition = "TEXT")
    private String userAnswer;

    private Boolean isCorrect;

    private Integer score;

    @Column(columnDefinition = "TEXT")
    private String aiFeedback;

    @Column(nullable = false, updatable = false)
    private LocalDateTime solvedAt;

    @PrePersist
    protected void onCreate() {
        this.solvedAt = LocalDateTime.now();
    }

    public void updateResult(Boolean isCorrect, Integer score, String aiFeedback) {
        this.isCorrect = isCorrect;
        this.score = score;
        this.aiFeedback = aiFeedback;
    }
}