package com.dyslexia.dyslexia.entity;

import com.dyslexia.dyslexia.enums.CompletionStatus;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "student_document_assignments",
       uniqueConstraints = @UniqueConstraint(columnNames = {"student_id", "document_id"}))
@Getter
@NoArgsConstructor
public class StudentDocumentAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", nullable = false)
    private Document document;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_by", nullable = false)
    private Teacher assignedBy;

    @Column(name = "current_page_number")
    private Integer currentPageNumber = 1;

    @Column(name = "assigned_at", nullable = false)
    private LocalDateTime assignedAt;

    @Column(name = "due_date")
    private LocalDateTime dueDate;
    
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(name = "completion_status", nullable = false)
    private CompletionStatus completionStatus = CompletionStatus.NOT_STARTED;

    @Column(name = "total_learning_time")
    private Integer totalLearningTime = 0;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public StudentDocumentAssignment(Student student, Document document, Teacher assignedBy,
                                    Integer currentPageNumber, LocalDateTime dueDate,
                                    CompletionStatus completionStatus, Integer totalLearningTime,
                                    LocalDateTime assignedAt, String notes) {
        this.student = student;
        this.document = document;
        this.assignedBy = assignedBy;
        this.currentPageNumber = currentPageNumber != null ? currentPageNumber : 1;
        this.assignedAt = assignedAt != null ? assignedAt : LocalDateTime.now();
        this.dueDate = dueDate;
        this.notes = notes;
        this.completionStatus = completionStatus != null ? completionStatus : CompletionStatus.NOT_STARTED;
        this.totalLearningTime = totalLearningTime != null ? totalLearningTime : 0;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void updateProgress(Integer currentPageNumber, CompletionStatus completionStatus, 
                              Integer additionalLearningTime) {
        if (currentPageNumber != null && currentPageNumber > this.currentPageNumber) {
            this.currentPageNumber = currentPageNumber;
        }
        
        if (completionStatus != null) {
            this.completionStatus = completionStatus;
        }
        
        if (additionalLearningTime != null && additionalLearningTime > 0) {
            this.totalLearningTime += additionalLearningTime;
        }
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }
    
    public Teacher getTeacher() {
        return this.assignedBy;
    }
} 