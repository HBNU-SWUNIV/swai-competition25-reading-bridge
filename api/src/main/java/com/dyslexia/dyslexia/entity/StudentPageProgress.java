package com.dyslexia.dyslexia.entity;

import com.dyslexia.dyslexia.enums.CompletionStatus;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "student_page_progress",
       uniqueConstraints = @UniqueConstraint(columnNames = {"student_id", "page_id"}))
@Getter
@Setter
@NoArgsConstructor
public class StudentPageProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "page_id", nullable = false)
    private Page page;

    @Column(name = "is_completed")
    private Boolean isCompleted = false;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "completion_status")
    private CompletionStatus completionStatus;

    @Column(name = "time_spent_seconds")
    private Integer timeSpentSeconds = 0;
    
    @Column(name = "retry_count")
    private Integer retryCount = 0;
    
    @Column(name = "comprehension_score")
    private Integer comprehensionScore;
    
    @Column(name = "used_tip_ids", length = 500)
    private String usedTipIds;

    @Column(name = "last_accessed_at")
    private LocalDateTime lastAccessedAt;

    @Column(name = "difficulty_rating")
    private Short difficultyRating;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public StudentPageProgress(Student student, Page page, Boolean isCompleted,
                              Integer timeSpentSeconds, LocalDateTime lastAccessedAt,
                              Short difficultyRating, String notes, CompletionStatus completionStatus,
                              Integer retryCount, Integer comprehensionScore, String usedTipIds) {
        this.student = student;
        this.page = page;
        this.isCompleted = isCompleted != null ? isCompleted : false;
        this.timeSpentSeconds = timeSpentSeconds != null ? timeSpentSeconds : 0;
        this.lastAccessedAt = lastAccessedAt != null ? lastAccessedAt : LocalDateTime.now();
        this.difficultyRating = difficultyRating;
        this.notes = notes;
        this.completionStatus = completionStatus;
        this.retryCount = retryCount != null ? retryCount : 0;
        this.comprehensionScore = comprehensionScore;
        this.usedTipIds = usedTipIds;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void updateProgress(Boolean isCompleted, Integer additionalTimeSpent, 
                              Short difficultyRating, String notes) {
        if (isCompleted != null) {
            this.isCompleted = isCompleted;
        }
        
        if (additionalTimeSpent != null && additionalTimeSpent > 0) {
            this.timeSpentSeconds += additionalTimeSpent;
        }
        
        this.lastAccessedAt = LocalDateTime.now();
        
        if (difficultyRating != null) {
            this.difficultyRating = difficultyRating;
        }
        
        if (notes != null) {
            this.notes = notes;
        }
    }
} 