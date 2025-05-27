package com.dyslexia.dyslexia.entity;

import com.dyslexia.dyslexia.enums.Grade;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "courses")
@Getter
@NoArgsConstructor
public class Course {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "teacherId")
  private Teacher teacher;

  private String subjectPath;

  private String title;

  private String type;

  @Enumerated(EnumType.STRING)
  private Grade grade;

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;

  private String state;

  @Builder
  public Course(Teacher teacher, String subjectPath, String title, String type, Grade grade,
      String state) {
    this.teacher = teacher;
    this.subjectPath = subjectPath;
    this.title = title;
    this.type = type;
    this.grade = grade;
    this.createdAt = LocalDateTime.now();
    this.updatedAt = LocalDateTime.now();
    this.state = state;
  }

  @PreUpdate
  public void preUpdate() {
    this.updatedAt = LocalDateTime.now();
  }
}
