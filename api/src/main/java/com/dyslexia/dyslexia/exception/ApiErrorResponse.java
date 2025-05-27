package com.dyslexia.dyslexia.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "API 오류 응답 모델")
public class ApiErrorResponse {
  private int status;
  private String error;
  private String message;
  private LocalDateTime timestamp;
}