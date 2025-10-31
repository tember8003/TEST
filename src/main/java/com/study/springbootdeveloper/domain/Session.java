package com.study.springbootdeveloper.domain;

import com.study.springbootdeveloper.type.Category;
import com.study.springbootdeveloper.type.DifficultyType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DifficultyType difficulty;

    @Column(nullable = false)
    private Integer totalQuestions;

    @Column(nullable = false)
    private Integer correctCount;

    @Column(nullable = false, updatable = false)
    private LocalDateTime startedAt;

    private LocalDateTime completedAt;

    private Integer timeLimitMinutes;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SolvedProblem> solvedProblems = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.startedAt = LocalDateTime.now();
        if (this.correctCount == null) {
            this.correctCount = 0;
        }
        if (this.totalQuestions == null) {
            this.totalQuestions = 10;
        }
    }

    public void complete() {
        this.completedAt = LocalDateTime.now();
    }

    public void incrementCorrectCount() {
        this.correctCount++;
    }
}