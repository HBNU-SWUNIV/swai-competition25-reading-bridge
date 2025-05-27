package com.dyslexia.dyslexia.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "일반 응답 DTO")
public class ResponseDto {

    @Schema(description = "처리 성공 여부", example = "true")
    private boolean success;

    @Schema(description = "응답 메시지", example = "처리가 완료되었습니다.")
    private String message;

    @Schema(description = "데이터")
    private Object data;
} 