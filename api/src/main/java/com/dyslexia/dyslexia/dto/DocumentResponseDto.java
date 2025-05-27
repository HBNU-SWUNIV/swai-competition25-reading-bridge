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
@Schema(description = "문서 업로드 응답 DTO")
public class DocumentResponseDto {

    @Schema(description = "처리 성공 여부", example = "true")
    private boolean success;

    @Schema(description = "응답 메시지", example = "PDF 업로드 완료. 비동기 처리가 시작되었습니다.")
    private String message;

    @Schema(description = "문서 정보")
    private DocumentDto document;
} 