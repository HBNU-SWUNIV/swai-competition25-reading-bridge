package com.dyslexia.dyslexia.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VocabularyAnalysisSearchRequest {
    private Long documentId;
    private Integer pageNumber;
    private String blockId;
} 