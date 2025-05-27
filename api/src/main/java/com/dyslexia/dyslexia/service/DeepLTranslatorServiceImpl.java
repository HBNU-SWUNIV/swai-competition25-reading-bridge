package com.dyslexia.dyslexia.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class DeepLTranslatorServiceImpl implements DeepLTranslatorService {

    @Value("${deepl.api.key}")
    private String apiKey;

    @Value("${deepl.api.url:https://api-free.deepl.com}")
    private String apiUrl;

    private final RestTemplate restTemplate;

    public DeepLTranslatorServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public String translateText(String text) {
        log.info("번역 시작: 텍스트 길이: {}", text.length());
        try {
            if (text.length() > 5000) {
                return translateLargeText(text);
            }
            String translatedText = callDeepLApi(text);
            log.info("번역 완료: 번역된 텍스트 길이: {}", translatedText.length());
            return translatedText;
        } catch (Exception e) {
            log.error("번역 실패: {}", e.getMessage());
            throw new RuntimeException("번역 실패: " + e.getMessage(), e);
        }
    }

    private String translateLargeText(String text) {
        log.info("대용량 텍스트 청킹 번역 시작: {}", text.length());
        List<String> chunks = new ArrayList<>();
        int chunkSize = 5000;
        for (int i = 0; i < text.length(); i += chunkSize) {
            int end = Math.min(i + chunkSize, text.length());
            chunks.add(text.substring(i, end));
        }
        List<String> translatedChunks = new ArrayList<>();
        for (String chunk : chunks) {
            translatedChunks.add(callDeepLApi(chunk));
            try { Thread.sleep(1000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }
        String result = String.join("", translatedChunks);
        log.info("대용량 텍스트 청킹 번역 완료");
        return result;
    }

    private String callDeepLApi(String text) {
        DeepLRequest request = new DeepLRequest();
        request.setText(text);
        request.setSourceLang("EN");
        request.setTargetLang("KO");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "DeepL-Auth-Key " + apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<DeepLRequest> entity = new HttpEntity<>(request, headers);
        ResponseEntity<DeepLResponse> response = restTemplate.postForEntity(
                apiUrl + "/v2/translate",
                entity,
                DeepLResponse.class
        );
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("DeepL API 호출 실패: " + response.getStatusCode());
        }
        return response.getBody().getTranslations().get(0).getText();
    }

    // 내부 요청/응답 클래스
    public static class DeepLRequest {
        private List<String> text;
        private String source_lang;
        private String target_lang;
        private String formality = "default";
        public void setText(String text) { this.text = List.of(text); }
        public void setSourceLang(String sourceLang) { this.source_lang = sourceLang; }
        public void setTargetLang(String targetLang) { this.target_lang = targetLang; }
        public List<String> getText() { return text; }
        public String getSource_lang() { return source_lang; }
        public String getTarget_lang() { return target_lang; }
        public String getFormality() { return formality; }
    }
    public static class DeepLResponse {
        private List<Translation> translations;
        public List<Translation> getTranslations() { return translations; }
        public void setTranslations(List<Translation> translations) { this.translations = translations; }
        public static class Translation {
            private String text;
            private String detected_source_language;
            public String getText() { return text; }
            public void setText(String text) { this.text = text; }
            public String getDetected_source_language() { return detected_source_language; }
            public void setDetected_source_language(String detected_source_language) { this.detected_source_language = detected_source_language; }
        }
    }
} 