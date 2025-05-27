package com.dyslexia.dyslexia.dto;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StudentReqDto {

  private String clientId;
  private Long teacherId;
  private String gradeLabel;
  private String type;
  private String state;
  private String profileImageUrl;
  private List<String> interests;
}
