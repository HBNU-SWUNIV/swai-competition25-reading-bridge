package com.dyslexia.dyslexia.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dyslexia.dyslexia.dto.AIResponseDto;
import com.dyslexia.dyslexia.service.OpenAIService;

import lombok.RequiredArgsConstructor;

import java.util.Map;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RequiredArgsConstructor
@RestController
@RequestMapping("/openai")
public class OpenAIController {

    private final OpenAIService openAIService;

    @PostMapping("/generate-text")
    public AIResponseDto generateText(@RequestBody String prompt) {
        return openAIService.generateText(prompt);
    }

    @Operation(
        summary = "텍스트를 오디오로 변환",
        description = "입력된 텍스트를 OpenAI Speech API로 변환하여 오디오(mpeg) blob으로 반환합니다. voice, model은 선택값입니다.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(
                schema = @Schema(
                    example = "{\"text\": \"안녕하세요. 테스트입니다.\", \"voice\": \"nova\", \"model\": \"tts-1\"}"
                )
            )
        )
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "오디오 데이터 반환", content = @Content(mediaType = "audio/mpeg")),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping(value = "/speech", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> createSpeech(@RequestBody Map<String, String> req) {
        String text = req.get("text");
        String voice = req.getOrDefault("voice", "nova");
        String model = req.getOrDefault("model", "tts-1");
        byte[] audio = openAIService.createSpeech(text, voice, model);
        return ResponseEntity.ok()
                .contentType(MediaType.valueOf("audio/mpeg"))
                .body(audio);
    }
}
