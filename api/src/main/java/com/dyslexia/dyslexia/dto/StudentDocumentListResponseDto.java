package com.dyslexia.dyslexia.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "학생 문서 목록 응답 DTO")
public class StudentDocumentListResponseDto {

    @Schema(description = "처리 성공 여부", example = "true")
    private boolean success;

    @Schema(description = "응답 메시지", example = "할당된 문서 목록 조회 성공")
    private String message;

    @Schema(description = "문서 목록")
    private List<DocumentDto> documents;
} 