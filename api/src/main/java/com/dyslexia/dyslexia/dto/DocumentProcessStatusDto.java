package com.dyslexia.dyslexia.dto;

import com.dyslexia.dyslexia.enums.DocumentProcessStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "문서 처리 상태 응답 DTO")
public class DocumentProcessStatusDto {

    @Schema(description = "처리 성공 여부", example = "true")
    private boolean success;

    @Schema(description = "응답 메시지", example = "문서 처리 상태 조회 성공")
    private String message;

    @Schema(description = "문서 ID", example = "1")
    private Long documentId;

    @Schema(description = "처리 상태", example = "PROCESSING")
    private DocumentProcessStatus status;

    @Schema(description = "처리 진행도 (0-100)", example = "65")
    private Integer progress;
} 