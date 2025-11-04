package com.study.springbootdeveloper.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // 메인 페이지
        registry.addViewController("/").setViewName("index");

        // 카테고리 선택 페이지
        registry.addViewController("/category").setViewName("category");

        // 문제 풀이 페이지
        registry.addViewController("/problems").setViewName("problem");

        // 통계 페이지
        registry.addViewController("/statistics").setViewName("statistics");

        // 챌린지 모드 페이지
        registry.addViewController("/challenge").setViewName("challenge");
    }
}