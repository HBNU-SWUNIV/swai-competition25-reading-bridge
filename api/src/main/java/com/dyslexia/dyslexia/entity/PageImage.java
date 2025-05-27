package com.dyslexia.dyslexia.entity;

import com.dyslexia.dyslexia.enums.ImageType;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Table(name = "page_images")
@Getter
@NoArgsConstructor
public class PageImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "page_id", nullable = false)
    private Page page;

    @Column(name = "image_url", nullable = false, length = 500)
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "image_type", nullable = false)
    private ImageType imageType;

    @Column(name = "concept_reference")
    private String conceptReference;

    @Column(name = "alt_text", columnDefinition = "TEXT")
    private String altText;

    @Column(name = "position_in_page", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private JsonNode positionInPage;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public PageImage(Page page, String imageUrl, ImageType imageType, String conceptReference,
                    String altText, JsonNode positionInPage) {
        this.page = page;
        this.imageUrl = imageUrl;
        this.imageType = imageType;
        this.conceptReference = conceptReference;
        this.altText = altText;
        this.positionInPage = positionInPage;
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