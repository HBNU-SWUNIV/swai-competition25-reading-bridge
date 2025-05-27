package com.dyslexia.dyslexia.dto;

import com.dyslexia.dyslexia.enums.ColorScheme;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "접근성 설정 업데이트 요청 DTO")
public class AccessibilitySettingsUpdateRequest {

    @Schema(description = "페이지 ID", example = "1")
    private Long pageId;

    @Schema(description = "학생 ID", example = "1")
    private Long studentId;

    @Schema(description = "문서 ID", example = "1")
    private Long documentId;

    @Schema(description = "글꼴 크기", example = "18")
    private Integer fontSize;

    @Schema(description = "줄 간격", example = "1.5")
    private Float lineSpacing;

    @Schema(description = "글자 간격", example = "1.2")
    private Float letterSpacing;

    @Schema(description = "색상 스키마", example = "DARK_MODE")
    private ColorScheme colorScheme;

    @Schema(description = "자동 음성 읽기 활성화", example = "true")
    private Boolean textToSpeechEnabled;

    @Schema(description = "읽기 강조 표시 활성화", example = "true")
    private Boolean readingHighlightEnabled;

    @Schema(description = "기본 설정으로 저장", example = "false")
    private Boolean saveAsDefault;

    @Schema(description = "문서 전체에 적용", example = "true")
    private Boolean applyToEntireDocument;

    @Schema(description = "사용자 정의 배경색", example = "#F5F5DC")
    private String customBackgroundColor;

    @Schema(description = "사용자 정의 글자색", example = "#333333")
    private String customTextColor;
}
