package com.dyslexia.dyslexia.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.dyslexia.dyslexia.domain.pdf.TextBlock;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class VocabularyAnalysisPromptService {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${ai.api.url}")
    private String aiApiUrl;
    @Value("${ai.api.key}")
    private String aiApiKey;

    private static final String MODEL = "gpt-4o-mini";

    // 기존 어휘 분석 프롬프트
    public String analyzeVocabulary(String text, int targetGradeLevel) {
        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", MODEL);

            List<Map<String, String>> messages = new ArrayList<>();

            Map<String, String> systemMessage = new HashMap<>();
            systemMessage.put("role", "system");
            systemMessage.put("content", getSystemPrompt());
            messages.add(systemMessage);

            Map<String, String> userMessage = new HashMap<>();
            userMessage.put("role", "user");
            userMessage.put("content", getUserPrompt(text, targetGradeLevel));
            messages.add(userMessage);

            requestBody.put("messages", messages);
            requestBody.put("temperature", 0.3);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + aiApiKey);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            Map<String, Object> response = restTemplate.postForObject(aiApiUrl, request, Map.class);
            log.info("어휘 분석 AI 요청: {}", objectMapper.writeValueAsString(requestBody));
            // log.info("어휘 분석 AI 응답: {}", response);

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
            Map<String, Object> choice = choices.get(0);
            Map<String, String> message = (Map<String, String>) choice.get("message");
            String content = message.get("content");

            if (content.contains("```json")) {
                content = content.substring(content.indexOf("```json") + 7);
                content = content.substring(0, content.indexOf("```"));
            } else if (content.contains("```")) {
                content = content.substring(content.indexOf("```") + 3);
                content = content.substring(0, content.indexOf("```"));
            }
            content = content.trim();
            // log.info("어휘 분석 결과 JSON: {}", content);
            return content;
        } catch (Exception e) {
            log.error("어휘 분석 프롬프트/AI 호출 실패", e);
            return "[]";
        }
    }

    public String analyzeVocabularyBasic(String text, int targetGradeLevel) {
        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", MODEL);

            List<Map<String, String>> messages = new ArrayList<>();

            Map<String, String> systemMessage = new HashMap<>();
            systemMessage.put("role", "system");
            systemMessage.put("content", getBasicSystemPrompt());
            messages.add(systemMessage);

            Map<String, String> userMessage = new HashMap<>();
            userMessage.put("role", "user");
            userMessage.put("content", getBasicUserPrompt(text, targetGradeLevel));
            messages.add(userMessage);

            requestBody.put("messages", messages);
            requestBody.put("temperature", 0.3);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + aiApiKey);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            Map<String, Object> response = restTemplate.postForObject(aiApiUrl, request, Map.class);
            log.info("어휘 분석(BASIC) AI 요청: {}", objectMapper.writeValueAsString(requestBody));
            // log.info("어휘 분석(BASIC) AI 응답: {}", response);

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
            Map<String, Object> choice = choices.get(0);
            Map<String, String> message = (Map<String, String>) choice.get("message");
            String content = message.get("content");

            if (content.contains("```json")) {
                content = content.substring(content.indexOf("```json") + 7);
                content = content.substring(0, content.indexOf("```"));
            } else if (content.contains("```")) {
                content = content.substring(content.indexOf("```") + 3);
                content = content.substring(0, content.indexOf("```"));
            }
            content = content.trim();
            // log.debug("어휘 분석(BASIC) 결과 JSON: {}", content);
            return content;
        } catch (Exception e) {
            log.error("어휘 분석(BASIC) 프롬프트/AI 호출 실패", e);
            return "[]";
        }
    }

    public String analyzePhonemeAnalysis(String word) {
        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", MODEL);

            List<Map<String, String>> messages = new ArrayList<>();

            Map<String, String> systemMessage = new HashMap<>();
            systemMessage.put("role", "system");
            systemMessage.put("content", getPhonemeSystemPrompt());
            messages.add(systemMessage);

            Map<String, String> userMessage = new HashMap<>();
            userMessage.put("role", "user");
            userMessage.put("content", getPhonemeUserPrompt(word));
            messages.add(userMessage);

            requestBody.put("messages", messages);
            requestBody.put("temperature", 0.3);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + aiApiKey);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            Map<String, Object> response = restTemplate.postForObject(aiApiUrl, request, Map.class);
            log.debug("음소분해/학습팁 AI 요청: {}", objectMapper.writeValueAsString(requestBody));
            // log.info("음소분해/학습팁 AI 응답: {}", response);

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
            Map<String, Object> choice = choices.get(0);
            Map<String, String> message = (Map<String, String>) choice.get("message");
            String content = message.get("content");

            if (content.contains("```json")) {
                content = content.substring(content.indexOf("```json") + 7);
                content = content.substring(0, content.indexOf("```"));
            } else if (content.contains("```")) {
                content = content.substring(content.indexOf("```") + 3);
                content = content.substring(0, content.indexOf("```"));
            }
            content = content.trim();
            // log.debug("음소분해/학습팁 결과 JSON: {}", content);
            return content;
        } catch (Exception e) {
            log.error("음소분해/학습팁 프롬프트/AI 호출 실패", e);
            return "{}";
        }
    }

    private String getSystemPrompt() {
        return "당신은 9-13세 난독증이 있는 초등학생을 위한 어휘 분석 전문가입니다.\n" +
                "주어진 문장에서 해당 연령대의 학생들이 읽고 이해하기 어려운 단어를 찾아서 JSON 형식으로 분석 결과를 제공해주세요.\n" +
                "어려운 어휘 판단 기준:\n" +
                "1. 3음절 이상의 복합어\n" +
                "2. 초등 3-4학년 수준을 넘는 어휘\n" +
                "3. 한자어 및 외래어 (단, 일상적으로 쓰이는 것 제외)\n" +
                "4. 추상적 개념어\n" +
                "5. 전문용어 및 학술용어\n" +
                "주의사항:\n" +
                "- 조사, 어미, 접속사는 제외\n" +
                "- 일상생활에서 자주 쓰이는 단어는 3음절 이상이라도 제외\n" +
                "- 학생의 연령과 인지 수준을 고려";
    }

    private String getUserPrompt(String text, int targetGradeLevel) {
        return String.format(
            "다음 문장에서 9-13세 난독증 학생이 어려워할 수 있는 어휘를 추출하고, 각 어휘를 음소 단위로 분해하고, 학습에 도움이 되는 팁까지 포함하여 아래 JSON 배열 형식으로만 응답하세요(코드블록 없이, 반드시 배열로):\n" +
            "\n문장: \"%s\"\n" +
            "\n예시:\n" +
            "[\n" +
            "  {\n" +
            "    \"word\": \"감각\",\n" +
            "    \"syllables\": [\n" +
            "      {\n" +
            "        \"syllable\": \"감\",\n" +
            "        \"order\": 1,\n" +
            "        \"components\": {\n" +
            "          \"initial\": {\"consonant\": \"ㄱ\", \"pronunciation\": \"기역\", \"sound\": \"/g/\", \"writingOrder\": 1, \"strokes\": 2, \"difficulty\": \"easy\"},\n" +
            "          \"medial\": {\"vowel\": \"ㅏ\", \"pronunciation\": \"아\", \"sound\": \"/a/\", \"writingOrder\": 2, \"strokes\": 2, \"difficulty\": \"easy\"},\n" +
            "          \"final\": {\"consonant\": \"ㅁ\", \"pronunciation\": \"미음\", \"sound\": \"/m/\", \"writingOrder\": 3, \"strokes\": 4, \"difficulty\": \"medium\"}\n" +
            "        },\n" +
            "        \"combinedSound\": \"/gam/\",\n" +
            "        \"writingTips\": \"ㄱ을 먼저 쓰고, ㅏ를 그 옆에, 마지막에 ㅁ을 아래에 써주세요\"\n" +
            "      }\n" +
            "    ],\n" +
            "    \"totalPhonemes\": {\n" +
            "      \"consonants\": [\"ㄱ\", \"ㅁ\", \"ㄱ\"],\n" +
            "      \"vowels\": [\"ㅏ\", \"ㅏ\"],\n" +
            "      \"uniquePhonemes\": [\"ㄱ\", \"ㅏ\", \"ㅁ\"]\n" +
            "    },\n" +
            "    \"difficultyLevel\": \"medium\",\n" +
            "    \"writingOrder\": [\n" +
            "      { \"step\": 1, \"phoneme\": \"ㄱ\", \"syllable\": \"감\" },\n" +
            "      { \"step\": 2, \"phoneme\": \"ㅏ\", \"syllable\": \"감\" },\n" +
            "      { \"step\": 3, \"phoneme\": \"ㅁ\", \"syllable\": \"감\" }\n" +
            "    ],\n" +
            "    \"learningTips\": {\n" +
            "      \"commonMistakes\": [\"ㅁ 받침을 빼먹기 쉬워요\"],\n" +
            "      \"practiceWords\": [\"강각\", \"감정\", \"각도\"],\n" +
            "      \"rhymingWords\": [\"암각\", \"담각\"]\n" +
            "    }\n" +
            "  }\n" +
            "]\n"
        , text);
    }

    private String getBasicSystemPrompt() {
        return "당신은 9-13세 난독증이 있는 초등학생을 위한 어휘 분석 전문가입니다.\n" +
                "주어진 문장에서 해당 연령대의 학생들이 읽고 이해하기 어려운 단어를 찾아서 아래 JSON 배열 형식으로 분석 결과를 제공해주세요.\n" +
                "어려운 어휘 판단 기준:\n" +
                "1. 3음절 이상의 복합어\n" +
                "2. 초등 3-4학년 수준을 넘는 어휘\n" +
                "3. 한자어 및 외래어 (단, 일상적으로 쓰이는 것 제외)\n" +
                "4. 추상적 개념어\n" +
                "5. 전문용어 및 학술용어\n" +
                "주의사항:\n" +
                "- 조사, 어미, 접속사는 제외\n" +
                "- 일상생활에서 자주 쓰이는 단어는 3음절 이상이라도 제외\n" +
                "- 학생의 연령과 인지 수준을 고려";
    }

    private String getBasicUserPrompt(String text, int targetGradeLevel) {
        return String.format(
            "다음 문장에서 9-13세 난독증 학생이 어려워할 수 있는 어휘를 추출하고 분석해주세요:\n" +
            "\n문장: \"%s\"\n" +
            "\n아래 JSON 배열 형식으로만 응답하세요(코드블록 없이, 반드시 배열로):\n" +
            "[\n" +
            "  {\n" +
            "    \"word\": \"감각\",\n" +
            "    \"startIndex\": 3,\n" +
            "    \"endIndex\": 5,\n" +
            "    \"definition\": \"몸으로 느끼고 알아차리는 능력\",\n" +
            "    \"simplifiedDefinition\": \"보고, 듣고, 만지면서 알아차리는 것\",\n" +
            "    \"examples\": [\"눈으로 보는 것도 감각이에요\"],\n" +
            "    \"difficultyLevel\": \"medium\",\n" +
            "    \"reason\": \"추상적 개념어\",\n" +
            "    \"gradeLevel\": 4\n" +
            "  }\n" +
            "]\n"
        , text);
    }

    private String getPhonemeSystemPrompt() {
        return "당신은 한글 음성학 전문가입니다. 주어진 한글 단어를 음소(초성, 중성, 종성) 단위로 정확하게 분해하여 난독증 학생의 음운 학습을 돕는 데이터를 제공해주세요.";
    }

    private String getPhonemeUserPrompt(String word) {
        return String.format(
            "다음 단어를 음소별로 분해하고 학습에 필요한 정보를 제공해주세요:\n" +
            "\n단어: \"%s\"\n" +
            "\n아래 JSON 형식으로만, 코드블록 없이, 반드시 모든 필드 포함하여 응답하세요. (예시를 참고하여 동일한 구조로 작성):\n" +
            "{\n" +
            "  \"word\": \"감각\",\n" +
            "  \"syllables\": [\n" +
            "    {\n" +
            "      \"syllable\": \"감\",\n" +
            "      \"order\": 1,\n" +
            "      \"components\": {\n" +
            "        \"initial\": {\n" +
            "          \"consonant\": \"ㄱ\",\n" +
            "          \"pronunciation\": \"기역\",\n" +
            "          \"sound\": \"/g/\",\n" +
            "          \"writingOrder\": 1,\n" +
            "          \"strokes\": 2,\n" +
            "          \"difficulty\": \"easy\"\n" +
            "        },\n" +
            "        \"medial\": {\n" +
            "          \"vowel\": \"ㅏ\",\n" +
            "          \"pronunciation\": \"아\",\n" +
            "          \"sound\": \"/a/\",\n" +
            "          \"writingOrder\": 2,\n" +
            "          \"strokes\": 2,\n" +
            "          \"difficulty\": \"easy\"\n" +
            "        },\n" +
            "        \"final\": {\n" +
            "          \"consonant\": \"ㅁ\",\n" +
            "          \"pronunciation\": \"미음\",\n" +
            "          \"sound\": \"/m/\",\n" +
            "          \"writingOrder\": 3,\n" +
            "          \"strokes\": 4,\n" +
            "          \"difficulty\": \"medium\"\n" +
            "        }\n" +
            "      },\n" +
            "      \"combinedSound\": \"/gam/\",\n" +
            "      \"writingTips\": \"ㄱ을 먼저 쓰고, ㅏ를 그 옆에, 마지막에 ㅁ을 아래에 써주세요\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"syllable\": \"각\",\n" +
            "      \"order\": 2,\n" +
            "      \"components\": {\n" +
            "        \"initial\": {\n" +
            "          \"consonant\": \"ㄱ\",\n" +
            "          \"pronunciation\": \"기역\",\n" +
            "          \"sound\": \"/g/\",\n" +
            "          \"writingOrder\": 1,\n" +
            "          \"strokes\": 2,\n" +
            "          \"difficulty\": \"easy\"\n" +
            "        },\n" +
            "        \"medial\": {\n" +
            "          \"vowel\": \"ㅏ\",\n" +
            "          \"pronunciation\": \"아\",\n" +
            "          \"sound\": \"/a/\",\n" +
            "          \"writingOrder\": 2,\n" +
            "          \"strokes\": 2,\n" +
            "          \"difficulty\": \"easy\"\n" +
            "        },\n" +
            "        \"final\": {\n" +
            "          \"consonant\": \"ㄱ\",\n" +
            "          \"pronunciation\": \"기역\",\n" +
            "          \"sound\": \"/k/\",\n" +
            "          \"writingOrder\": 3,\n" +
            "          \"strokes\": 2,\n" +
            "          \"difficulty\": \"easy\"\n" +
            "        }\n" +
            "      },\n" +
            "      \"combinedSound\": \"/gak/\",\n" +
            "      \"writingTips\": \"ㄱ을 먼저 쓰고, ㅏ를 그 옆에, 마지막에 작은 ㄱ을 아래에 써주세요\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"totalPhonemes\": {\n" +
            "    \"consonants\": [\"ㄱ\", \"ㅁ\", \"ㄱ\"],\n" +
            "    \"vowels\": [\"ㅏ\", \"ㅏ\"],\n" +
            "    \"uniquePhonemes\": [\"ㄱ\", \"ㅏ\", \"ㅁ\"]\n" +
            "  },\n" +
            "  \"difficultyLevel\": \"medium\",\n" +
            "  \"writingOrder\": [\n" +
            "    { \"step\": 1, \"phoneme\": \"ㄱ\", \"syllable\": \"감\" },\n" +
            "    { \"step\": 2, \"phoneme\": \"ㅏ\", \"syllable\": \"감\" },\n" +
            "    { \"step\": 3, \"phoneme\": \"ㅁ\", \"syllable\": \"감\" },\n" +
            "    { \"step\": 4, \"phoneme\": \"ㄱ\", \"syllable\": \"각\" },\n" +
            "    { \"step\": 5, \"phoneme\": \"ㅏ\", \"syllable\": \"각\" },\n" +
            "    { \"step\": 6, \"phoneme\": \"ㄱ\", \"syllable\": \"각\" }\n" +
            "  ],\n" +
            "  \"learningTips\": {\n" +
            "    \"commonMistakes\": [\"ㅁ 받침을 빼먹기 쉬워요\", \"두 번째 ㄱ 받침을 크게 쓰기 쉬워요\"],\n" +
            "    \"practiceWords\": [\"강각\", \"감정\", \"각도\"],\n" +
            "    \"rhymingWords\": [\"암각\", \"담각\"]\n" +
            "  }\n" +
            "}\n"
        , word);
    }

    // 배치 어휘 분석 프롬프트
    /**
     * 여러 텍스트 블록을 한 번에 분석하는 메서드 (기본 어휘 분석)
     */
    public String analyzeVocabularyBatchBasic(List<TextBlock> textBlocks, int targetGradeLevel) {
        try {
            // 모든 텍스트를 구분자와 함께 연결
            String combinedText = textBlocks.stream()
                .map(block -> {
                    return "BLOCK_ID:" + block.getId() + "\n" + block.getText();
                })
                .collect(Collectors.joining("\n---BLOCK_SEPARATOR---\n"));
            
            // 요청 본문 생성
            Map<String, Object> requestBody = new HashMap<>();

            // 모델 설정
            requestBody.put("model", MODEL);
            
            // 메시지 생성
            List<Map<String, String>> messages = new ArrayList<>();

            // 시스템 메시지 추가
            Map<String, String> systemMessage = new HashMap<>();
            systemMessage.put("role", "system");
            systemMessage.put("content", getBatchSystemPrompt());
            messages.add(systemMessage);
            
            // 사용자 메시지 추가
            Map<String, String> userMessage = new HashMap<>();
            userMessage.put("role", "user");
            userMessage.put("content", getBatchUserPrompt(combinedText, targetGradeLevel));
            messages.add(userMessage);

            // 요청 본문 설정
            requestBody.put("messages", messages);
            requestBody.put("temperature", 0.3);

            // 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + aiApiKey);

            // 요청 생성
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            // AI 요청
            Map<String, Object> response = restTemplate.postForObject(aiApiUrl, request, Map.class);
            log.info("배치 어휘 분석 AI 요청: {}", objectMapper.writeValueAsString(requestBody));

            // 응답 처리
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
            Map<String, Object> choice = choices.get(0);
            Map<String, String> messageContent = (Map<String, String>) choice.get("message");
            String responseContent = messageContent.get("content");

            // JSON 형식 처리
            if (responseContent.contains("```json")) {
                responseContent = responseContent.substring(responseContent.indexOf("```json") + 7);
                responseContent = responseContent.substring(0, responseContent.indexOf("```"));
            } else if (responseContent.contains("```")) {
                responseContent = responseContent.substring(responseContent.indexOf("```") + 3);
                responseContent = responseContent.substring(0, responseContent.indexOf("```"));
            }
            responseContent = responseContent.trim();
            // log.info("배치 어휘 분석 결과 JSON: {}", responseContent);
            return responseContent;
        } catch (Exception e) {
            log.error("배치 어휘 분석 실패", e);
            return "{}";
        }
    }

    /*
     * 배치 어휘 분석을 위한 시스템 프롬프트
     */
    private String getBatchSystemPrompt() {
        return "당신은 9-13세 난독증이 있는 초등학생을 위한 어휘 및 음운 분석 전문가입니다.\n" +
               "여러 텍스트 블록을 분석하여 각 블록별로 학생들이 읽고 이해하기 어려운 단어를 추출하고, 각 단어에 대해 음운 분석(초성, 중성, 종성, 쓰기 순서 등)까지 포함하여 JSON으로 제공하세요.\n" +
               "각 블록은 '---BLOCK_SEPARATOR---'로 구분되며, 각 블록은 'BLOCK_ID:'로 시작하는 ID를 가집니다.\n\n" +
               "## 반드시 아래 TypeScript 스키마를 따라야 합니다. 누락 없이 모든 필드를 포함하세요. ##\n" +
               "```typescript\n" +
               "export interface PhonemeAnalysis {\n" +
               "  syllables: SyllableInfo[];\n" +
               "}\n" +
               "export interface SyllableInfo {\n" +
               "  character: string;\n" +
               "  pronunciation: string;\n" +
               "  difficulty?: number;\n" +
               "  syllable: string;\n" +
               "  writingTips: string;\n" +
               "  examples?: string[];\n" +
               "  components: {\n" +
               "    initial?: { consonant: string; pronunciation: string; };\n" +
               "    medial?: { vowel: string; pronunciation: string; };\n" +
               "    final?: { consonant: string; pronunciation: string; };\n" +
               "  };\n" +
               "}\n" +
               "```\n" +
               "각 단어의 phoneme 필드는 반드시 위 구조를 따라야 하며, 모든 필드를 빠짐없이 포함해야 합니다.\n\n" +
               "## 필수 지침: 각 텍스트 블록마다 반드시 하나 이상의 단어를 추출하고, 각 단어에 대해 음운 분석 정보를 포함하세요. ##\n" +
               "이 요구사항은 어떤 상황에서도 준수되어야 하며, 각 블록 ID에 대해 반드시 단어와 그 단어의 음운 분석이 포함되어야 합니다.\n" +
               "명백하게 어려운 단어가 없는 블록이라도, 해당 블록에서 가장 중요하거나 의미 있는 단어(명사, 동사, 형용사 등)를 선택하여 추출하고, 반드시 음운 분석 정보를 포함하세요.\n" +
               "블록 내용이 단 한 문장이거나 매우 짧더라도 반드시 단어와 음운 분석 정보를 포함해야 합니다.\n\n" +
               "어려운 어휘 판단 기준:\n" +
               "1. 3음절 이상의 복합어\n" +
               "2. 초등 3-4학년 수준을 넘는 어휘\n" +
               "3. 한자어 및 외래어 (단, 일상적으로 쓰이는 것 제외)\n" +
               "4. 추상적 개념어\n" +
               "5. 전문용어 및 학술용어\n\n" +
               "주의사항:\n" +
               "- 조사, 어미, 접속사는 제외\n" +
               "- 일상생활에서 자주 쓰이는 단어는 3음절 이상이라도 제외\n" +
               "- 학생의 연령과 인지 수준을 고려하세요.\n" +
               "- 명백하게 어려운 단어가 없다면, 내용 이해에 중요한 핵심 단어를 선택하세요.\n" +
               "- 단어 선택이 어렵더라도 반드시 각 블록 ID가 결과에 포함되어야 하며, 각 ID에 대해 반드시 단어와 음운 분석 정보가 포함되어야 합니다.\n\n" +
               "## 최종 확인사항: ##\n" +
               "1. 응답에서 모든 블록 ID를 키로 가지는 JSON 객체가 생성되어야 합니다.\n" +
               "2. 각 블록 ID 키에는 반드시 하나 이상의 단어와 그 단어의 음운 분석 정보가 포함된 배열이 있어야 합니다.\n";
    }

    /*
     * 배치 어휘 분석을 위한 사용자 프롬프트
     */
    private String getBatchUserPrompt(String text, int targetGradeLevel) {
        return String.format(
            "다음 텍스트 블록들을 분석하세요. 각 블록은 '---BLOCK_SEPARATOR---'로 구분되며, 각 블록은 'BLOCK_ID:'로 시작하는 ID를 가집니다.\n\n" +
            "텍스트 블록:\n\"%s\"\n\n" +
            "## 반드시 아래 TypeScript 스키마를 따라야 합니다. 누락 없이 모든 필드를 포함하세요. ##\n" +
            "```typescript\n" +
            "export interface PhonemeAnalysis {\n" +
            "  syllables: SyllableInfo[];\n" +
            "}\n" +
            "export interface SyllableInfo {\n" +
            "  character: string;\n" +
            "  pronunciation: string;\n" +
            "  difficulty?: number;\n" +
            "  syllable: string;\n" +
            "  writingTips: string;\n" +
            "  examples?: string[];\n" +
            "  components: {\n" +
            "    initial?: { consonant: string; pronunciation: string; };\n" +
            "    medial?: { vowel: string; pronunciation: string; };\n" +
            "    final?: { consonant: string; pronunciation: string; };\n" +
            "  };\n" +
            "}\n" +
            "```\n" +
            "각 단어의 phoneme 필드는 반드시 위 구조를 따라야 하며, 모든 필드를 빠짐없이 포함해야 합니다.\n\n" +
            "## 필수 요구사항 ##\n" +
            "1. 예외 없이 모든 블록에 대해 반드시 하나 이상의 단어를 추출하고, 각 단어에 대해 음운 분석 정보를 포함해야 합니다. 빈 배열은 절대 허용되지 않습니다.\n" +
            "2. 명확하게 어려운 단어가 없더라도, 해당 블록에서 가장 중요한 명사, 동사, 형용사를 선택하고 반드시 음운 분석 정보를 포함하세요.\n" +
            "3. 블록 ID를 정확히 추출하여 응답 JSON에 모든 블록 ID가 키로 포함되어야 합니다.\n" +
            "4. 단어가 한 두 개뿐인 매우 짧은 블록이라도 핵심 단어와 음운 분석 정보를 반드시 포함하세요.\n" +
            "5. 분석을 시작하기 전에 각 블록의 ID와 텍스트 내용을 명확히 식별하세요.\n\n" +
            "각 블록별로 단어와 음운 분석 정보를 분석하여 다음과 같은 JSON 형식으로 응답하세요. 블록 ID를 키로 사용하고, 각 키에는 해당 블록에서 찾은 단어와 그 단어의 음운 분석 정보가 포함된 배열을 제공하세요:\n" +
            "{\n" +
            "  \"blockId1\": [\n" +
            "    {\n" +
            "      \"word\": \"감각\",\n" +
            "      \"startIndex\": 3,\n" +
            "      \"endIndex\": 5,\n" +
            "      \"definition\": \"몸으로 느끼고 알아차리는 능력\",\n" +
            "      \"phoneme\": {\n" +
            "        \"syllables\": [\n" +
            "          {\n" +
            "            \"character\": \"감\",\n" +
            "            \"pronunciation\": \"gam\",\n" +
            "            \"syllable\": \"감\",\n" +
            "            \"writingTips\": \"ㄱ을 먼저 쓰고, ㅏ를 그 옆에, 마지막에 ㅁ을 아래에 써주세요\",\n" +
            "            \"components\": {\n" +
            "              \"initial\": { \"consonant\": \"ㄱ\", \"pronunciation\": \"기역\" },\n" +
            "              \"medial\": { \"vowel\": \"ㅏ\", \"pronunciation\": \"아\" },\n" +
            "              \"final\": { \"consonant\": \"ㅁ\", \"pronunciation\": \"미음\" }\n" +
            "            }\n" +
            "          },\n" +
            "          {\n" +
            "            \"character\": \"각\",\n" +
            "            \"pronunciation\": \"gak\",\n" +
            "            \"syllable\": \"각\",\n" +
            "            \"writingTips\": \"ㄱ을 먼저 쓰고, ㅏ를 그 옆에, 마지막에 ㄱ을 아래에 써주세요\",\n" +
            "            \"components\": {\n" +
            "              \"initial\": { \"consonant\": \"ㄱ\", \"pronunciation\": \"기역\" },\n" +
            "              \"medial\": { \"vowel\": \"ㅏ\", \"pronunciation\": \"아\" },\n" +
            "              \"final\": { \"consonant\": \"ㄱ\", \"pronunciation\": \"기역\" }\n" +
            "            }\n" +
            "          }\n" +
            "        ]\n" +
            "      }\n" +
            "    }\n" +
            "  ],\n" +
            "  \"blockId2\": [\n" +
            "    // 이 블록의 어려운 단어들 (최소 하나 이상, 각 단어에 phoneme 정보 포함)\n" +
            "  ]\n" +
            "}\n\n" +
            "## 절대적 중요 사항 ##\n" +
            "- 모든 블록 ID는 반드시 결과 JSON에 키로 포함되어야 합니다.\n" +
            "- 각 블록마다 반드시 최소 하나 이상의 단어와 그 단어의 음운 분석 정보가 배열에 포함되어야 합니다.\n" +
            "- 단어가 없거나 어려운 단어가 없는 블록이라도 가장 중요한 단어와 음운 분석 정보를 선택하세요.\n" +
            "- JSON 형식이 정확해야 하며, 모든 필수 필드가 포함되어야 합니다.\n" +
            "- 블록 ID는 숫자만 포함해야 합니다(예: \"12\"가 \"blockId12\"가 아님).\n\n" +
            "응답 전에 모든 블록 ID가 포함되었는지, 각 배열이 비어있지 않고 각 단어에 phoneme 정보가 포함되어 있는지 반드시 확인하세요.",
            text);
    }

    /*
     * 배치 음운 분석을 위한 시스템 프롬프트
     */
    private String getBatchPhonemeSystemPrompt() {
        return "당신은 한글 음성학 전문가입니다. 주어진 여러 한글 단어를 음소(초성, 중성, 종성) 단위로 분해하여 난독증 학생의 음운 학습을 돕는 데이터를 제공해주세요.\n" +
               "여러 단어를 한 번에 분석하고, 각 단어에 대한 분석 결과를 JSON 객체로 제공하세요.";
    }

    /*
     * 배치 음운 분석을 위한 사용자 프롬프트
     */
    private String getBatchPhonemeUserPrompt(String words) {
        return String.format(
            "다음 단어들을 각각 음소별로 분해하고 학습에 필요한 정보를 제공해주세요:\n" +
            "\n단어 목록: %s\n\n" +
            "아래 JSON 형식으로만 응답하세요. 단어를 키로 사용하고, 각 키에는 해당 단어의 음운 분석 결과를 제공하세요:\n" +
            "{\n" +
            "  \"감각\": {\n" +
            "    \"word\": \"감각\",\n" +
            "    \"syllables\": [\n" +
            "      {\n" +
            "        \"syllable\": \"감\",\n" +
            "        \"order\": 1,\n" +
            "        \"components\": {\n" +
            "          \"initial\": {\"consonant\": \"ㄱ\", \"pronunciation\": \"기역\", \"sound\": \"/g/\", \"writingOrder\": 1, \"strokes\": 2, \"difficulty\": \"easy\"},\n" +
            "          \"medial\": {\"vowel\": \"ㅏ\", \"pronunciation\": \"아\", \"sound\": \"/a/\", \"writingOrder\": 2, \"strokes\": 2, \"difficulty\": \"easy\"},\n" +
            "          \"final\": {\"consonant\": \"ㅁ\", \"pronunciation\": \"미음\", \"sound\": \"/m/\", \"writingOrder\": 3, \"strokes\": 4, \"difficulty\": \"medium\"}\n" +
            "        },\n" +
            "        \"combinedSound\": \"/gam/\",\n" +
            "        \"writingTips\": \"ㄱ을 먼저 쓰고, ㅏ를 그 옆에, 마지막에 ㅁ을 아래에 써주세요\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"syllable\": \"각\",\n" +
            "        \"order\": 2\n" +
            "        // 생략...\n" +
            "      }\n" +
            "    ],\n" +
            "    \"totalPhonemes\": {\n" +
            "      \"consonants\": [\"ㄱ\", \"ㅁ\", \"ㄱ\"],\n" +
            "      \"vowels\": [\"ㅏ\", \"ㅏ\"],\n" +
            "      \"uniquePhonemes\": [\"ㄱ\", \"ㅏ\", \"ㅁ\"]\n" +
            "    },\n" +
            "    \"difficultyLevel\": \"medium\",\n" +
            "    \"writingOrder\": [ // 생략... ],\n" +
            "    \"learningTips\": { // 생략... }\n" +
            "  },\n" +
            "  \"다른단어\": {\n" +
            "    // 다른 단어의 분석 결과\n" +
            "  }\n" +
            "}\n",
            words);
    }

    /**
     * 여러 단어의 음운 분석을 한 번에 수행
     */
    public Map<String, Object> batchAnalyzePhonemes(Set<String> words) {
        if (words.isEmpty()) {
            return Collections.emptyMap();
        }
        
        try {
            String wordsList = String.join(", ", words);
            log.info("음운 분석 시작: 총 {} 단어", words.size());
            
            // 요청 본문 생성
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", MODEL);

            // 메시지 생성
            List<Map<String, String>> messages = new ArrayList<>();

            // 시스템 메시지 추가
            Map<String, String> systemMessage = new HashMap<>();
            systemMessage.put("role", "system");
            systemMessage.put("content", getBatchPhonemeSystemPrompt());
            messages.add(systemMessage);

            // 사용자 메시지 추가
            Map<String, String> userMessage = new HashMap<>();
            userMessage.put("role", "user");
            userMessage.put("content", getBatchPhonemeUserPrompt(wordsList));
            messages.add(userMessage);

            // 요청 본문 설정
            requestBody.put("messages", messages);
            requestBody.put("temperature", 0.3);

            // 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + aiApiKey);

            // 요청 생성
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            // AI 요청
            Map<String, Object> response = restTemplate.postForObject(aiApiUrl, request, Map.class);
            log.info("배치 음운 분석 AI 요청: {}", objectMapper.writeValueAsString(requestBody));

            // 응답 처리
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
            Map<String, Object> choice = choices.get(0);
            Map<String, String> message = (Map<String, String>) choice.get("message");
            String content = message.get("content");

            // JSON 형식 처리
            if (content.contains("```json")) {
                content = content.substring(content.indexOf("```json") + 7);
                content = content.substring(0, content.indexOf("```"));
            } else if (content.contains("```")) {
                content = content.substring(content.indexOf("```") + 3);
                content = content.substring(0, content.indexOf("```"));
            }
            content = content.trim();
            
            // log.debug("배치 음운 분석 결과 JSON: {}", content);
            
            // JSON 응답을 Map<String, Object>으로 변환
            return objectMapper.readValue(content, 
                new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            log.error("배치 음운 분석 실패", e);
            return Collections.emptyMap();
        }
    }
} 