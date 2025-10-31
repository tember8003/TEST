package com.study.springbootdeveloper.config;

import com.study.springbootdeveloper.service.ProblemDataLoader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile("!test") // 테스트 환경에서는 실행하지 않음
public class DataInitializer implements CommandLineRunner {

    private final ProblemDataLoader problemDataLoader;

    @Value("${app.data.init.enabled:true}")
    private boolean initEnabled;

    @Value("${app.data.init.file-path:data.json}")
    private String filePath;

    public DataInitializer(ProblemDataLoader problemDataLoader) {
        this.problemDataLoader = problemDataLoader;
    }

    @Override
    public void run(String... args) {
        if (!initEnabled) {
            log.info("데이터 초기화가 비활성화되어 있습니다.");
            return;
        }

        try {
            log.info("=".repeat(60));
            log.info("문제 데이터 초기화 시작...");
            log.info("파일 경로: {}", filePath);
            log.info("=".repeat(60));

            problemDataLoader.loadProblemsFromJson(filePath);

            log.info("=".repeat(60));
            log.info("문제 데이터 초기화 완료!");
            log.info("=".repeat(60));

        } catch (Exception e) {
            log.error("=".repeat(60));
            log.error("문제 데이터 초기화 실패", e);
            log.error("=".repeat(60));
            // 실패해도 애플리케이션은 계속 실행
        }
    }
}