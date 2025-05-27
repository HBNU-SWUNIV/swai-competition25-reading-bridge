package com.dyslexia.dyslexia.mapper.custom;

import com.dyslexia.dyslexia.enums.Grade;
import org.springframework.stereotype.Component;

@Component
public class GradeMapper {
  public Grade toEnum(String label) {
    return label == null ? null : Grade.fromLabel(label);
  }

  public String toLabel(Grade grade) {
    return grade == null ? null : grade.getLabel();
  }
}