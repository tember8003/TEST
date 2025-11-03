package com.study.springbootdeveloper.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    /**
     * 메인 화면
     */
    @GetMapping("/")
    public String index() {
        return "index";
    }

    /**
     * 카테고리 선택 화면
     */
    @GetMapping("/category")
    public String category() {
        return "category";
    }

    /**
     * 문제 풀이 화면 (자유 선택 모드)
     */
    @GetMapping("/problems")
    public String problems() {
        return "problem";
    }

    /**
     * 통계 대시보드
     */
    @GetMapping("/statistics")
    public String statistics() {
        return "statistics";
    }

    /**
     * 챌린지 모드 설정 화면
     */
    @GetMapping("/challenge")
    public String challenge() {
        return "challenge";
    }

    /**
     * 챌린지 풀이 화면
     */
    @GetMapping("/challenge-solve")
    public String challengeSolve() {
        return "challenge-solve";
    }
}