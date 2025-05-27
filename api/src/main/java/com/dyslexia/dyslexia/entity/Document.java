package com.dyslexia.dyslexia.entity;

import com.dyslexia.dyslexia.enums.DocumentProcessStatus;
import com.dyslexia.dyslexia.enums.Grade;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "documents")
@Getter
@NoArgsConstructor
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    private Teacher teacher;

    @Column(nullable = false)
    private String title;

    @Column(name = "original_filename", nullable = false)
    private String originalFilename;

    @Setter
    @Column(name = "file_path", nullable = true, length = 500)
    private String filePath;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "mime_type", length = 100)
    private String mimeType;

    @Column(name = "page_count")
    private Integer pageCount;

    @Enumerated(EnumType.STRING)
    private Grade grade;

    @Column(name = "subject_path")
    private String subjectPath;

    @Enumerated(EnumType.STRING)
    @Column(name = "process_status", nullable = false)
    private DocumentProcessStatus processStatus = DocumentProcessStatus.PENDING;

    private String state;
    private String type;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private String metadata;

    @OneToMany(mappedBy = "document", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Page> pages = new ArrayList<>();

    @Builder
    public Document(Teacher teacher, String title, String originalFilename, String filePath,
                   Long fileSize, String mimeType, Integer pageCount, Grade grade,
                   String subjectPath, String state, String type, String metadata,
                   DocumentProcessStatus processStatus) {
        this.teacher = teacher;
        this.title = title;
        this.originalFilename = originalFilename;
        this.filePath = filePath;
        this.fileSize = fileSize;
        this.mimeType = mimeType;
        this.pageCount = pageCount;
        this.grade = grade;
        this.subjectPath = subjectPath;
        this.state = state;
        this.type = type;
        this.metadata = metadata;
        this.processStatus = processStatus != null ? processStatus : DocumentProcessStatus.PENDING;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void setProcessStatus(DocumentProcessStatus processStatus) {
        this.processStatus = processStatus;
    }
    
    public void setPageCount(Integer pageCount) {
        this.pageCount = pageCount;
    }

    public void addPage(Page page) {
        this.pages.add(page);
        if (page.getDocument() != this) {
            page.setDocument(this);
        }
    }
}
