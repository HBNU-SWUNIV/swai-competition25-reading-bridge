package com.dyslexia.dyslexia.dto;

import com.dyslexia.dyslexia.enums.Grade;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CourseReqDto {

  private Long teacherId;
  private String subjectPath;
  private String title;
  private String type;
  private Grade grade;
  private String state;
}