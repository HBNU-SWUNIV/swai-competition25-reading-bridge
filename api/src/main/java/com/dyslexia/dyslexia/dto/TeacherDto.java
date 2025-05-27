package com.dyslexia.dyslexia.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TeacherDto {

  private Long id;
  private String clientId;
  private String name;
  private String organization;
  private String profileImageUrl;
}
