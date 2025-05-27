package com.dyslexia.dyslexia.entity;

import com.dyslexia.dyslexia.enums.TermType;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Table(name = "page_tips")
@Getter
@NoArgsConstructor
public class PageTip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "page_id", nullable = false)
    private Page page;

    @Column(nullable = false)
    private String term;

    @Column(name = "simplified_explanation", nullable = false, columnDefinition = "TEXT")
    private String simplifiedExplanation;

    @Column(name = "term_position", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private JsonNode termPosition;

    @Enumerated(EnumType.STRING)
    @Column(name = "term_type", nullable = false)
    private TermType termType;

    @Column(name = "visual_aid_needed")
    private Boolean visualAidNeeded = false;

    @Column(name = "read_aloud_text", columnDefinition = "TEXT")
    private String readAloudText;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public PageTip(Page page, String term, String simplifiedExplanation, JsonNode termPosition,
                  TermType termType, Boolean visualAidNeeded, String readAloudText) {
        this.page = page;
        this.term = term;
        this.simplifiedExplanation = simplifiedExplanation;
        this.termPosition = termPosition;
        this.termType = termType;
        this.visualAidNeeded = visualAidNeeded != null ? visualAidNeeded : false;
        this.readAloudText = readAloudText;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void setPage(Page page) {
        this.page = page;
    }
} 