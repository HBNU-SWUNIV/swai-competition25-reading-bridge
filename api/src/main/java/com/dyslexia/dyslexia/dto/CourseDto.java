package com.dyslexia.dyslexia.dto;

import com.dyslexia.dyslexia.enums.Grade;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CourseDto {

  private Long id;
  private Long teacherId;
  private String subjectPath;
  private String title;
  private String type;
  private Grade grade;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private String state;
}
