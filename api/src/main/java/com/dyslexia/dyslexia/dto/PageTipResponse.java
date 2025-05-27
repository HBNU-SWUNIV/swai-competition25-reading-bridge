package com.dyslexia.dyslexia.dto;

import com.dyslexia.dyslexia.entity.PageTip;
import com.dyslexia.dyslexia.enums.TermType;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "페이지 팁 응답")
public class PageTipResponse {

    @Schema(description = "팁 ID")
    private Long id;

    @Schema(description = "페이지 ID")
    private Long pageId;

    @Schema(description = "용어")
    private String term;

    @Schema(description = "간소화된 설명")
    private String simplifiedExplanation;

    @Schema(description = "용어 위치 정보 (JSON)")
    private JsonNode termPosition;

    @Schema(description = "용어 타입", example = "VOCABULARY")
    private TermType termType;

    @Schema(description = "시각적 보조 필요 여부")
    private Boolean visualAidNeeded;

    @Schema(description = "읽기 텍스트")
    private String readAloudText;

    @Schema(description = "생성 시간")
    private LocalDateTime createdAt;

    @Schema(description = "수정 시간")
    private LocalDateTime updatedAt;

    public static PageTipResponse fromEntity(PageTip pageTip) {
        return PageTipResponse.builder()
                .id(pageTip.getId())
                .pageId(pageTip.getPage().getId())
                .term(pageTip.getTerm())
                .simplifiedExplanation(pageTip.getSimplifiedExplanation())
                .termPosition(pageTip.getTermPosition())
                .termType(pageTip.getTermType())
                .visualAidNeeded(pageTip.getVisualAidNeeded())
                .readAloudText(pageTip.getReadAloudText())
                .createdAt(pageTip.getCreatedAt())
                .updatedAt(pageTip.getUpdatedAt())
                .build();
    }
} 