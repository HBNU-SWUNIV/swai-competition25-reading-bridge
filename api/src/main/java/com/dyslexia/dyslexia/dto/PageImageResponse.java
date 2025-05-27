package com.dyslexia.dyslexia.dto;

import com.dyslexia.dyslexia.entity.PageImage;
import com.dyslexia.dyslexia.enums.ImageType;
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
@Schema(description = "페이지 이미지 응답")
public class PageImageResponse {

    @Schema(description = "이미지 ID")
    private Long id;

    @Schema(description = "페이지 ID")
    private Long pageId;

    @Schema(description = "이미지 URL")
    private String imageUrl;

    @Schema(description = "이미지 타입", example = "DIAGRAM")
    private ImageType imageType;

    @Schema(description = "개념 참조")
    private String conceptReference;

    @Schema(description = "대체 텍스트")
    private String altText;

    @Schema(description = "페이지 내 위치 정보 (JSON)")
    private JsonNode positionInPage;

    @Schema(description = "생성 시간")
    private LocalDateTime createdAt;

    @Schema(description = "수정 시간")
    private LocalDateTime updatedAt;

    public static PageImageResponse fromEntity(PageImage pageImage) {
        return PageImageResponse.builder()
                .id(pageImage.getId())
                .pageId(pageImage.getPage().getId())
                .imageUrl(pageImage.getImageUrl())
                .imageType(pageImage.getImageType())
                .conceptReference(pageImage.getConceptReference())
                .altText(pageImage.getAltText())
                .positionInPage(pageImage.getPositionInPage())
                .createdAt(pageImage.getCreatedAt())
                .updatedAt(pageImage.getUpdatedAt())
                .build();
    }
} 