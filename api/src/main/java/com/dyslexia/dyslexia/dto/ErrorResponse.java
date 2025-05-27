package com.dyslexia.dyslexia.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "에러 응답 DTO")
public class ErrorResponse {
    @Schema(description = "에러 코드", example = "401")
    private final int code;

    @Schema(description = "에러 메시지", example = "인증되지 않은 사용자입니다.")
    private final String message;

    @Schema(description = "상세 에러 메시지", example = "JWT 토큰이 유효하지 않습니다.")
    private final String detail;
} 