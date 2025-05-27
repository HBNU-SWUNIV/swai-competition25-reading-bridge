package com.dyslexia.dyslexia.dto;

import com.dyslexia.dyslexia.enums.DocumentProcessStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "페이지 정보 DTO")
public class PageDto {

    @Schema(description = "페이지 ID", example = "1")
    private Long id;

    @Schema(description = "문서 ID", example = "1")
    private Long documentId;

    @Schema(description = "페이지 번호", example = "1")
    private Integer pageNumber;

    @Schema(description = "섹션 제목", example = "생태계의 구성 요소")
    private String sectionTitle;

    @Schema(description = "읽기 난이도 (1-10)", example = "5")
    private Integer readingLevel;

    @Schema(description = "단어 수", example = "250")
    private Integer wordCount;

    @Schema(description = "복잡도 점수 (0.0-1.0)", example = "0.6")
    private Float complexityScore;

    @Schema(description = "처리 상태", example = "COMPLETED")
    private DocumentProcessStatus processingStatus;

    @Schema(description = "생성 시간")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @Schema(description = "수정 시간")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
} 