package com.dyslexia.dyslexia.domain.pdf;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "vocabulary_analysis")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class VocabularyAnalysis {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long documentId;
    private Integer pageNumber;
    private String blockId;

    private String word;
    private Integer startIndex;
    private Integer endIndex;

    private String definition;
    private String simplifiedDefinition;
    @Column(columnDefinition = "TEXT")
    private String examples;

    private String difficultyLevel;
    private String reason;
    private Integer gradeLevel;

    @Column(columnDefinition = "TEXT")
    private String phonemeAnalysisJson;

    private LocalDateTime createdAt;
} 