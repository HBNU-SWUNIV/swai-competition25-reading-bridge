package com.dyslexia.dyslexia.dto;

import com.dyslexia.dyslexia.entity.Page;
import com.dyslexia.dyslexia.enums.DocumentProcessStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.dyslexia.dyslexia.domain.pdf.Block;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;
import java.util.Collections;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "페이지 콘텐츠 응답")
public class PageContentResponse {

    @Schema(description = "페이지 ID")
    private Long id;

    @Schema(description = "문서 ID")
    private Long documentId;

    @Schema(description = "페이지 번호")
    private Integer pageNumber;

    @Schema(description = "원본 콘텐츠")
    private String originalContent;

    @Schema(description = "처리된 콘텐츠 (JSON)")
    private JsonNode processedContent;

    @Schema(description = "처리 상태", example = "COMPLETED")
    private DocumentProcessStatus processingStatus;

    @Schema(description = "섹션 제목")
    private String sectionTitle;

    @Schema(description = "읽기 레벨")
    private Integer readingLevel;

    @Schema(description = "단어 수")
    private Integer wordCount;

    @Schema(description = "복잡도 점수")
    private Float complexityScore;

    @Schema(description = "생성 시간")
    private LocalDateTime createdAt;

    @Schema(description = "수정 시간")
    private LocalDateTime updatedAt;

    @Schema(description = "Block 구조화 데이터. type: HEADING1, TEXT, LIST, IMAGE, TABLE, PAGE_TIP, PAGE_IMAGE 등. 예시: [ {\"id\":\"1\",\"type\":\"HEADING1\",\"text\":\"챕터 제목\"}, {\"id\":\"2\",\"type\":\"TEXT\",\"text\":\"본문\"}, {\"id\":\"3\",\"type\":\"LIST\",\"items\":[\"항목1\",\"항목2\"]} ]",
           example = "[{\"id\":\"1\",\"type\":\"HEADING1\",\"text\":\"챕터 제목\"},{\"id\":\"2\",\"type\":\"TEXT\",\"text\":\"본문\"},{\"id\":\"3\",\"type\":\"LIST\",\"items\":[\"항목1\",\"항목2\"]}]")
    private List<Block> blocks;

    public static PageContentResponse fromEntity(Page page, ObjectMapper objectMapper) {
        List<Block> blocks = null;
        try {
            if (page.getProcessedContent() != null && !page.getProcessedContent().isNull()) {
                blocks = objectMapper.readValue(page.getProcessedContent().toString(), new TypeReference<List<Block>>() {});
            } else {
                blocks = Collections.emptyList();
            }
        } catch (Exception e) {
            blocks = Collections.emptyList();
        }
        return PageContentResponse.builder()
                .id(page.getId())
                .documentId(page.getDocument().getId())
                .pageNumber(page.getPageNumber())
                .originalContent(page.getOriginalContent())
                .processedContent(page.getProcessedContent())
                .processingStatus(page.getProcessingStatus())
                .sectionTitle(page.getSectionTitle())
                .readingLevel(page.getReadingLevel())
                .wordCount(page.getWordCount())
                .complexityScore(page.getComplexityScore())
                .createdAt(page.getCreatedAt())
                .updatedAt(page.getUpdatedAt())
                .blocks(blocks)
                .build();
    }
} 