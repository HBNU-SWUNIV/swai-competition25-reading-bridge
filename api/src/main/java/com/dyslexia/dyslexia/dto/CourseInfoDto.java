package com.dyslexia.dyslexia.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CourseInfoDto {

  private Long id;
  private Long courseId;
  private Long studentId;
  private Long teacherId;
  private Integer learningTime;
  private Integer page;
  private Integer maxPage;
}
