package com.dyslexia.dyslexia.dto;

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
@Schema(description = "문서 할당 요청 DTO")
public class DocumentAssignmentRequest {

    @Schema(description = "선생님 ID", example = "1", required = true)
    private Long teacherId;

    @Schema(description = "학생 ID", example = "1", required = true)
    private Long studentId;

    @Schema(description = "문서 ID", example = "1", required = true)
    private Long documentId;

    @Schema(description = "제출 기한", example = "2023-12-31T23:59:59")
    private LocalDateTime dueDate;

    @Schema(description = "메모", example = "이 문서를 다음 주까지 완료해 주세요.")
    private String notes;
} 