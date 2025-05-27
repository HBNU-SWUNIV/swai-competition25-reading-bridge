package com.dyslexia.dyslexia.entity;

import com.dyslexia.dyslexia.enums.ColorScheme;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "page_accessibility_settings",
       uniqueConstraints = @UniqueConstraint(columnNames = {"student_id", "page_id"}))
@Getter
@Setter
@NoArgsConstructor
public class PageAccessibilitySettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "page_id")
    private Page page;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", nullable = false)
    private Document document;

    @Column(name = "font_family")
    private String fontFamily = "OpenDyslexic";

    @Column(name = "font_size")
    private Integer fontSize = 16;

    @Column(name = "line_spacing")
    private Float lineSpacing = 1.5f;

    @Column(name = "letter_spacing")
    private Float letterSpacing = 0.1f;

    @Column(name = "word_spacing")
    private Float wordSpacing = 0.2f;

    @Enumerated(EnumType.STRING)
    @Column(name = "color_scheme", nullable = false)
    private ColorScheme colorScheme = ColorScheme.LIGHT;

    @Column(name = "text_to_speech_enabled")
    private Boolean textToSpeechEnabled = false;

    @Column(name = "reading_highlight_enabled")
    private Boolean readingHighlightEnabled = true;

    @Column(name = "custom_text_color", length = 30)
    private String customTextColor = "#000000";

    @Column(name = "custom_background_color", length = 30)
    private String customBackgroundColor = "#FFFFFF";

    @Column(name = "last_modified_at")
    private LocalDateTime lastModifiedAt;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public PageAccessibilitySettings(Student student, Page page, Document document, String fontFamily,
                                    Integer fontSize, Float lineSpacing, Float letterSpacing,
                                    Float wordSpacing, ColorScheme colorScheme,
                                    Boolean textToSpeechEnabled, Boolean readingHighlightEnabled, 
                                    String customTextColor, String customBackgroundColor) {
        this.student = student;
        this.page = page;
        this.document = document;
        this.fontFamily = fontFamily != null ? fontFamily : "OpenDyslexic";
        this.fontSize = fontSize != null ? fontSize : 16;
        this.lineSpacing = lineSpacing != null ? lineSpacing : 1.5f;
        this.letterSpacing = letterSpacing != null ? letterSpacing : 0.1f;
        this.wordSpacing = wordSpacing != null ? wordSpacing : 0.2f;
        this.colorScheme = colorScheme != null ? colorScheme : ColorScheme.LIGHT;
        this.textToSpeechEnabled = textToSpeechEnabled != null ? textToSpeechEnabled : false;
        this.readingHighlightEnabled = readingHighlightEnabled != null ? readingHighlightEnabled : true;
        this.customTextColor = customTextColor != null ? customTextColor : "#000000";
        this.customBackgroundColor = customBackgroundColor != null ? customBackgroundColor : "#FFFFFF";
        this.lastModifiedAt = LocalDateTime.now();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void updateSettings(String fontFamily, Integer fontSize, Float lineSpacing,
                              Float letterSpacing, Float wordSpacing, ColorScheme colorScheme,
                              Boolean textToSpeechEnabled, Boolean readingHighlightEnabled,
                              String customTextColor, String customBackgroundColor) {
        if (fontFamily != null) {
            this.fontFamily = fontFamily;
        }
        if (fontSize != null) {
            this.fontSize = fontSize;
        }
        if (lineSpacing != null) {
            this.lineSpacing = lineSpacing;
        }
        if (letterSpacing != null) {
            this.letterSpacing = letterSpacing;
        }
        if (wordSpacing != null) {
            this.wordSpacing = wordSpacing;
        }
        if (colorScheme != null) {
            this.colorScheme = colorScheme;
        }
        if (textToSpeechEnabled != null) {
            this.textToSpeechEnabled = textToSpeechEnabled;
        }
        if (readingHighlightEnabled != null) {
            this.readingHighlightEnabled = readingHighlightEnabled;
        }
        if (customTextColor != null) {
            this.customTextColor = customTextColor;
        }
        if (customBackgroundColor != null) {
            this.customBackgroundColor = customBackgroundColor;
        }
        this.lastModifiedAt = LocalDateTime.now();
    }
} 