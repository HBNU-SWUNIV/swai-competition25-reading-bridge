package com.dyslexia.dyslexia.enums;

import lombok.Getter;

@Getter
public enum Grade {
  GRADE_1("1학년"),
  GRADE_2("2학년"),
  GRADE_3("3학년"),
  GRADE_4("4학년"),
  GRADE_5("5학년"),
  GRADE_6("6학년");

  private final String label;

  Grade(String label) {
    this.label = label;
  }

  public static Grade fromLabel(String label) {
    for (Grade grade : Grade.values()) {
      if (grade.label.equals(label)) {
        return grade;
      }
    }

    throw new IllegalArgumentException("Unknown grade label: " + label);
  }
}
