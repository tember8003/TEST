package com.study.springbootdeveloper.service;

import com.study.springbootdeveloper.domain.Problem;
import com.study.springbootdeveloper.dto.response.GradingResultDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class GeminiApiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url:https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * 단답형/서술형 답변 채점
     */
    public GradingResultDto gradeAnswer(Problem problem, String userAnswer) {
        String prompt = buildGradingPrompt(problem, userAnswer);
        String response = callGeminiApi(prompt);
        return parseGradingResponse(response);
    }

    /**
     * 객관식 문제 보충 설명 생성
     */
    public String generateExplanation(Problem problem, String userAnswer, boolean isCorrect) {
        String prompt = buildExplanationPrompt(problem, userAnswer, isCorrect);
        return callGeminiApi(prompt);
    }

    /**
     * 채점용 프롬프트 생성
     */
    private String buildGradingPrompt(Problem problem, String userAnswer) {
        return String.format("""
            당신은 Java Spring 전문가입니다. 다음 답변을 채점해주세요.
            
            [문제]
            %s
            
            [정답]
            %s
            
            [사용자 답변]
            %s
            
            위 사용자 답변이 정답과 일치하는지 판단해주세요.
            완벽하게 일치하지 않아도, 핵심 개념을 이해하고 있다면 정답으로 인정합니다.
            
            응답은 반드시 다음 형식으로만 작성해주세요:
            판정: [정답/부분정답/오답]
            점수: [0-100 사이의 정수]
            피드백: [상세한 설명]
            """,
                problem.getQuestion(),
                problem.getAnswer(),
                userAnswer
        );
    }

    /**
     * 보충 설명용 프롬프트 생성
     */
    private String buildExplanationPrompt(Problem problem, String userAnswer, boolean isCorrect) {
        if (isCorrect) {
            return String.format("""
                다음 문제에 대해 정답을 맞춘 학생에게 보충 설명을 해주세요.
                
                [문제]
                %s
                
                [정답]
                %s
                
                [기본 해설]
                %s
                
                정답을 맞췄지만 더 깊이 이해할 수 있도록 추가 설명, 실무 팁, 주의사항 등을 알려주세요.
                """,
                    problem.getQuestion(),
                    problem.getAnswer(),
                    problem.getExplanation()
            );
        } else {
            return String.format("""
                다음 문제에 대해 오답을 선택한 학생에게 설명해주세요.
                
                [문제]
                %s
                
                [정답]
                %s
                
                [학생이 선택한 답]
                %s
                
                [기본 해설]
                %s
                
                왜 틀렸는지, 정답과의 차이점, 그리고 올바른 개념을 명확하게 설명해주세요.
                """,
                    problem.getQuestion(),
                    problem.getAnswer(),
                    userAnswer,
                    problem.getExplanation()
            );
        }
    }

    /**
     * Gemini API 호출
     */
    @SuppressWarnings("unchecked")
    private String callGeminiApi(String prompt) {
        try {
            String url = apiUrl + "?key=" + apiKey;

            // 요청 바디 구성 (명확한 타입 지정)
            Map<String, String> part = new HashMap<>();
            part.put("text", prompt);

            Map<String, Object> content = new HashMap<>();
            content.put("parts", new Map[]{part});

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("contents", new Map[]{content});

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);

            // 응답 파싱 (명시적 타입 캐스팅)
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();

                List<Map<String, Object>> candidates =
                        (List<Map<String, Object>>) responseBody.get("candidates");

                if (candidates != null && !candidates.isEmpty()) {
                    Map<String, Object> candidate = candidates.get(0);
                    Map<String, Object> contentResponse =
                            (Map<String, Object>) candidate.get("content");
                    List<Map<String, Object>> parts =
                            (List<Map<String, Object>>) contentResponse.get("parts");

                    if (parts != null && !parts.isEmpty()) {
                        Map<String, Object> partResponse = parts.get(0);
                        return (String) partResponse.get("text");
                    }
                }
            }

            log.error("Gemini API 응답 파싱 실패");
            return "AI 피드백 생성에 실패했습니다.";

        } catch (Exception e) {
            log.error("Gemini API 호출 실패", e);
            return "AI 피드백 생성 중 오류가 발생했습니다.";
        }
    }

    /**
     * 채점 응답 파싱
     */
    private GradingResultDto parseGradingResponse(String response) {
        try {
            // "판정: 정답" 형식 파싱
            String[] lines = response.split("\n");
            String judgement = "";
            int score = 0;
            StringBuilder feedback = new StringBuilder();

            for (String line : lines) {
                if (line.startsWith("판정:")) {
                    judgement = line.substring(4).trim();
                } else if (line.startsWith("점수:")) {
                    String scoreStr = line.substring(4).trim().replaceAll("[^0-9]", "");
                    score = Integer.parseInt(scoreStr);
                } else if (line.startsWith("피드백:")) {
                    feedback.append(line.substring(5).trim());
                } else if (!line.trim().isEmpty() && feedback.length() > 0) {
                    feedback.append("\n").append(line);
                }
            }

            boolean isCorrect = judgement.contains("정답") && !judgement.contains("부분");

            return GradingResultDto.builder()
                    .isCorrect(isCorrect)
                    .score(score)
                    .feedback(feedback.toString().trim())
                    .build();

        } catch (Exception e) {
            log.error("채점 응답 파싱 실패", e);

            return GradingResultDto.builder()
                    .isCorrect(false)
                    .score(0)
                    .feedback("채점 결과 파싱에 실패했습니다.")
                    .build();
        }
    }

}