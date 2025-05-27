package com.dyslexia.dyslexia.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service()
@RequiredArgsConstructor
public class DocumentDetailAdder {
    private final ImageGenerator imageGenerator;
    private final AIPromptService aiPromptService;

    public String getTitleImagePrompt(String rawContent) {
        String documentInfo = extractDocumentInfo(rawContent);
        String promptTemplate = """
            PDF의 첫 페이지 자료이고, 해당 자료의 유형을 판단해서 책 표지 이미지를 만들어줘
            %s

            지시사항:
            - 아동을 위한 친화적인 동화같은 그림체로 그려주세요
            - 밝고 명확한 색상과 단순한 형태를 사용해주세요
            - 시각적 혼란을 줄 수 있는 복잡한 배경은 제거해주세요
            - 글자나 텍스트는 포함하지 마세요
            - 주요 개념을 상징적으로 표현해주세요
            - 9-13세 아동의 관심을 끌 수 있도록 흥미로운 요소를 포함해주세요
            - 2:3 비율의 세로형 이미지로 생성해주세요
            """;
        String format = String.format(promptTemplate, documentInfo);

        log.info("표지 이미지 프롬프트: {}" , format);

        return aiPromptService.promptToOpenAI(format);
    }

    public String getConceptImagePrompt(String rawContent) {
        String documentInfo = extractDocumentInfo(rawContent);
        String promptTemplate = """
            개념 시각화 이미지: %s

            주제 맥락: %s
            
            지시사항:
            - 난독증이 있는 아동이 쉽게 이해할 수 있는 간결한 그림체로 표현해주세요
            - 핵심 개념을 직관적으로 이해할 수 있게 시각화해주세요
            - 개념 간의 관계가 있다면 화살표나 연결선으로 명확하게 표시해주세요
            - 색상 대비를 높게 유지하고 패턴이나 질감은 단순하게 표현해주세요
            - 배경은 최소화하고 주요 요소에 집중할 수 있게 해주세요
            - 텍스트 없이 시각적 요소만으로 개념을 전달해주세요
            - 2:3 비율의 이미지로 생성해주세요
            """;
        String format = String.format(promptTemplate, documentInfo);

        log.info("이미지 컨셉트 프롬프트: {}" , format);

        return aiPromptService.promptToOpenAI(format);
    }

    public String getDocumentImageConcept(String rawContent) {
        String contentToAnalyze = rawContent.length() > 1500 ? rawContent.substring(0, 1500) : rawContent;

        String conceptPrompt = """
            다음은 교육 자료의 첫 페이지입니다. 이 자료의 주제와 대상 연령을 파악하고,
            전체 문서에서 사용할 일관된 이미지 스타일 가이드를 작성해주세요.
            
            1. 주제 분야(과학, 역사, 문학 등)
            2. 대상 연령대
            3. 추천 색상 팔레트(4-5가지 색상)
            4. 적합한 그림 스타일(예: 동화적, 사실적, 만화적)
            5. 이미지에 포함할 요소
            6. 이미지에서 피해야 할 요소
            
            자료 첫 페이지:
            %s
            """;

        return String.format(conceptPrompt, contentToAnalyze);
    }

    public String generateTitleImage(String rawContent) {
        String prompt = getTitleImagePrompt(rawContent);
        log.info("prompt: {}", prompt);
        return imageGenerator.generateImage(prompt, "16:9");
    }

    public String generateConceptImage(String concept, String aspectRatio) {
        String ratio = (aspectRatio == null || aspectRatio.isEmpty()) ? "16:9" : aspectRatio;
        String prompt = getConceptImagePrompt(concept);
        log.info("prompt: {}", prompt);
        return imageGenerator.generateImage(prompt, ratio);
    }

    private String extractDocumentInfo(String rawContent) {
        String contentToAnalyze = rawContent.length() > 1000 ? rawContent.substring(0, 1000) : rawContent;

        String extractionPrompt = """
            다음은 교육 자료의 첫 페이지입니다. 이 자료가 어떤 주제를 다루는지, 어떤 대상을 위한 것인지 분석해주세요.
            결과는 100단어 이내로 간결하게 요약해주세요.
            
            자료 첫 페이지:
            %s
            """;

        String prompt = String.format(extractionPrompt, contentToAnalyze);
        String result = aiPromptService.promptToOpenAI(prompt);

        return (result != null && !result.trim().isEmpty()) ? result : contentToAnalyze;
    }
}