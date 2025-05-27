package com.dyslexia.dyslexia.service;

import com.dyslexia.dyslexia.dto.AIResponseDto;
import com.dyslexia.dyslexia.entity.AIResponse;
import com.dyslexia.dyslexia.entity.AIRquest;
import com.dyslexia.dyslexia.repository.AIRequestRepository;
import com.dyslexia.dyslexia.repository.AIResponseRepository;
import io.github.sashirestela.openai.SimpleOpenAI;
import io.github.sashirestela.openai.domain.chat.ChatMessage.SystemMessage;
import io.github.sashirestela.openai.domain.chat.ChatMessage.UserMessage;
import io.github.sashirestela.openai.domain.chat.ChatRequest;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OpenAIService {

  private final AIRequestRepository aiRequestRepository;
  private final AIResponseRepository aiResponseRepository;
  private final SystemMessage systemMessage = SystemMessage.of(
      "[입력 데이터]\n- 제공된 문서(PDF에서 추출된 텍스트)\n\n[대상]\n- 대상: 난독증 아동 (읽기와 이해에 어려움을 겪는 학생)\n- 연령/학습 수준: 초등학교 고학년 또는 중학생\n\n[목표 및 변환 기준]\n- 텍스트를 쉽고 명료하게 재작성하여 단문 위주로 구성할 것.\n- 어려운 단어와 비유적 표현은 사용하지 않고, 필요한 경우 핵심 용어에 간단한 설명(예: 괄호 안에 쉬운 단어 설명)을 추가할 것.\n- 문맥을 고려해 전체 교과서의 흐름과 연결되도록 할 것.\n- 중요한 정보는 누락 없이 전달할 것.\n- 핵심 개념은 굵은 글씨로 강조할 것.\n- 추상적인 개념에는 구체적 예시를 추가할 것.\n- 개념 설명에 필요한 시각화 요소를 제안할 것.\n\n[출력 형식]\n- 마크다운 형식\n- 단원의 개요\n- 단원 목차(핵심 콜아웃 포인트)\n- 단원 내용(각 콜아웃에 대한 설명 문구)\n- 변환 과정에서의 주의 사항이나 추가 설명");
  @Value("${OPENAI_API_KEY}")
  private String OPENAI_API_KEY;
  @Value("${ai.api.url:https://api.openai.com/v1}")
  private String OPENAI_API_URL;
  private String openaiModel = "o3-mini";
  private final RestTemplate restTemplate = new RestTemplate();

  @Transactional
  public AIResponseDto generateText(String prompt) {
    AIResponseDto responseDto;

    try {
      log.info("OpenAI API 호출 시작 - 프롬프트 길이: {}", prompt.length());
      SimpleOpenAI openAI = setSimpleOpenAI();

      ChatRequest chatRequest = setRequest(prompt, systemMessage);
      try {
        var futureChat = openAI.chatCompletions().create(chatRequest);
        log.info("OpenAI API 호출 완료, 응답 대기 중");

        var chatResponse = futureChat.join();
        log.info("OpenAI API 응답 수신 완료");

        String content = chatResponse.firstContent();
        log.info("응답 내용 길이: {}", content.length());
        saveEntity(prompt, "OpenAI API 호출 성공", "success", content);

        responseDto = AIResponseDto.builder().output(content).message("OpenAI API 호출 성공")
            .status("success").build();

      } catch (Exception e) {
        log.error("OpenAI API 응답 처리 중 오류: {}", e.getMessage(), e);
        saveEntity(prompt, "OpenAI API 호출 중 오류 발생", "error", "");
        throw e;
      }
      return responseDto;

    } catch (Exception e) {
      log.error("OpenAI API 호출 중 오류 발생: {}", e.getMessage(), e);
      return AIResponseDto.builder().output("").message("오류 발생: " + e.getMessage()).status("error")
          .build();
    }
  }

  private void saveEntity(String prompt, String message, String status, String content) {
    AIRquest aiRequest = AIRquest.builder().prompt(prompt).model(openaiModel)
        .createdAt(LocalDateTime.now()).build();
    aiRequest = aiRequestRepository.save(aiRequest);

    AIResponse aiResponse = AIResponse.builder().request(aiRequest).output(content).message(message)
        .status(status).createdAt(LocalDateTime.now()).build();

    aiResponseRepository.save(aiResponse);
    log.debug("AIResponse DB 저장 완료: requestId={}", aiRequest.getId());

    aiRequest.setResponse(aiResponse);
    aiRequest = aiRequestRepository.save(aiRequest);
    log.debug("AIRequest DB 저장 완료: id={}", aiRequest.getId());
  }

  // OpenAI API 인스턴스 생성
  private SimpleOpenAI setSimpleOpenAI() {
    SimpleOpenAI openAI = SimpleOpenAI.builder()
        .apiKey(validateApiKey(OPENAI_API_KEY)) // OpenAI API 키 유효성 검사
        .build();
    return openAI;
  }

  // Request 설정
  private ChatRequest setRequest(String prompt, SystemMessage systemMessage) {
    ChatRequest chatRequest = ChatRequest.builder().model(openaiModel).message(systemMessage)
        .message(UserMessage.of(prompt)).build();
    log.info("OpenAI API 요청 생성 완료: model = {}", openaiModel);

    return chatRequest;
  }

  // API KEY 유효성 검사
  private String validateApiKey(String apiKey) {
    if (apiKey == null || apiKey.trim().isEmpty()) {
      log.warn("OpenAI API 키를 찾을 수 없습니다");
      throw new IllegalStateException("API 키가 설정되지 않았습니다");
    }
    log.debug("API 키 유효성 확인: {}", apiKey.substring(0, Math.min(10, apiKey.length())) + "...");

    return apiKey;
  }

  // OpenAI 모델 설정
  public void setOpenaiModel(String openaiModel) {
    this.openaiModel = openaiModel;
  }

  public byte[] createSpeech(String text, String voice, String model) {
    String url = "https://api.openai.com/v1/audio/speech";
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setBearerAuth(OPENAI_API_KEY);
    Map<String, Object> body = Map.of(
      "model", model != null ? model : "gpt-4o-mini-tts",
      "input", text,
      "voice", voice != null ? voice : "echo"
    );
    HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
    ResponseEntity<byte[]> response = restTemplate.exchange(
      url, HttpMethod.POST, entity, byte[].class
    );
    if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
      throw new RuntimeException("OpenAI Speech API 호출 실패: " + response.getStatusCode());
    }
    return response.getBody();
  }
}
