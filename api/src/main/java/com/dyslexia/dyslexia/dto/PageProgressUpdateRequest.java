package com.dyslexia.dyslexia.dto;

import com.dyslexia.dyslexia.enums.CompletionStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "페이지 진행 상태 업데이트 요청 DTO")
public class PageProgressUpdateRequest {

    @Schema(description = "페이지 ID", example = "1")
    private Long pageId;

    @Schema(description = "학생 ID", example = "1")
    private Long studentId;

    @Schema(description = "진행 시간(초)", example = "300")
    private Integer timeSpentSeconds;

    @Schema(description = "완료 상태", example = "COMPLETED")
    private CompletionStatus completionStatus;

    @Schema(description = "재시도 횟수", example = "2")
    private Integer retryCount;

    @Schema(description = "사용된 팁 ID 목록", example = "[1, 2, 3]")
    private Long[] usedTipIds;

    @Schema(description = "이해도 점수 (0-100)", example = "85")
    private Integer comprehensionScore;

    @Schema(description = "메모", example = "어려운 개념들이 많아서 시간이 오래 걸렸습니다.")
    private String notes;
} 