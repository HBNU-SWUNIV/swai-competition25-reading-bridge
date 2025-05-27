package com.dyslexia.dyslexia.dto;

import com.dyslexia.dyslexia.entity.Page;
import com.dyslexia.dyslexia.entity.PageAccessibilitySettings;
import com.dyslexia.dyslexia.entity.PageImage;
import com.dyslexia.dyslexia.entity.PageTip;
import com.dyslexia.dyslexia.entity.StudentPageProgress;
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
@Schema(description = "페이지 상세 정보 응답 DTO")
public class PageDetailResponseDto {

    @Schema(description = "처리 성공 여부", example = "true")
    private boolean success;

    @Schema(description = "응답 메시지", example = "페이지 상세 정보 조회 성공")
    private String message;

    @Schema(description = "페이지 정보")
    private Page page;

    @Schema(description = "페이지 팁 목록")
    private List<PageTip> tips;

    @Schema(description = "페이지 이미지 목록")
    private List<PageImage> images;

    @Schema(description = "학생의 페이지 진행 상태")
    private StudentPageProgress progress;

    @Schema(description = "학생의 페이지 접근성 설정")
    private PageAccessibilitySettings settings;
}