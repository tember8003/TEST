package com.study.springbootdeveloper.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.springbootdeveloper.domain.Problem;
import com.study.springbootdeveloper.dto.ProblemsWrapper;
import com.study.springbootdeveloper.repository.ProblemRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProblemDataLoader {

    private final ProblemRepository problemRepository;
    private final ObjectMapper objectMapper;

    public ProblemDataLoader(ProblemRepository problemRepository, ObjectMapper objectMapper) {
        this.problemRepository = problemRepository;
        this.objectMapper = objectMapper;
    }

    /**
     * JSON 파일에서 문제 데이터 로딩
     */
    @Transactional
    public void loadProblemsFromJson(String filePath) {
        try {
            log.info("문제 데이터 로딩 시작: {}", filePath);

            // JSON 파일 읽기
            ClassPathResource resource = new ClassPathResource(filePath);
            InputStream inputStream = resource.getInputStream();

            // JSON 파싱
            ProblemsWrapper wrapper = objectMapper.readValue(inputStream, ProblemsWrapper.class);

            if (wrapper.getQuestions() == null || wrapper.getQuestions().isEmpty()) {
                log.warn("JSON 파일에 문제가 없습니다.");
                return;
            }

            // Entity로 변환
            List<Problem> problems = wrapper.getQuestions().stream()
                    .map(dto -> dto.toEntity())
                    .collect(Collectors.toList());

            // 기존 데이터 확인 (중복 방지)
            long existingCount = problemRepository.count();
            if (existingCount > 0) {
                log.info("이미 {}개의 문제가 존재합니다. 로딩을 건너뜁니다.", existingCount);
                return;
            }

            // DB에 저장
            List<Problem> savedProblems = problemRepository.saveAll(problems);
            log.info("총 {}개의 문제가 성공적으로 로딩되었습니다.", savedProblems.size());

            // 카테고리별 통계 출력
            savedProblems.stream()
                    .collect(Collectors.groupingBy(Problem::getCategory, Collectors.counting()))
                    .forEach((category, count) ->
                            log.info("  - {}: {}개", category.getDescription(), count));

        } catch (IOException e) {
            log.error("JSON 파일 읽기 실패: {}", filePath, e);
            throw new RuntimeException("문제 데이터 로딩 실패", e);
        } catch (Exception e) {
            log.error("문제 데이터 로딩 중 오류 발생", e);
            throw new RuntimeException("문제 데이터 로딩 실패", e);
        }
    }

    /**
     * 특정 카테고리 문제만 로딩
     */
    @Transactional
    public void loadProblemsByCategory(String filePath, String categoryFilter) {
        try {
            log.info("카테고리 필터링 로딩 시작: {} - {}", filePath, categoryFilter);

            ClassPathResource resource = new ClassPathResource(filePath);
            InputStream inputStream = resource.getInputStream();

            ProblemsWrapper wrapper = objectMapper.readValue(inputStream, ProblemsWrapper.class);

            List<Problem> problems = wrapper.getQuestions().stream()
                    .filter(dto -> dto.getCategory().equalsIgnoreCase(categoryFilter))
                    .map(dto -> dto.toEntity())
                    .collect(Collectors.toList());

            if (problems.isEmpty()) {
                log.warn("필터링된 문제가 없습니다.");
                return;
            }

            List<Problem> savedProblems = problemRepository.saveAll(problems);
            log.info("{}개의 {}카테고리 문제가 로딩되었습니다.", savedProblems.size(), categoryFilter);

        } catch (IOException e) {
            log.error("JSON 파일 읽기 실패", e);
            throw new RuntimeException("문제 데이터 로딩 실패", e);
        }
    }

    /**
     * 데이터 초기화 (개발/테스트용)
     */
    @Transactional
    public void clearAllProblems() {
        long count = problemRepository.count();
        problemRepository.deleteAll();
        log.info("{}개의 문제 데이터가 삭제되었습니다.", count);
    }
}