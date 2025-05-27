package com.dyslexia.dyslexia.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CourseInfoReqDto {

  private Long courseId;
  private Long studentId;
  private Long teacherId;
  private Integer learningTime;
  private Integer page;
  private Integer maxPage;
}
