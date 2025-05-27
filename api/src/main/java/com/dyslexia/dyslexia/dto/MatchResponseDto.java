package com.dyslexia.dyslexia.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MatchResponseDto {

    private Long id;
    private String organization;
    private String profileImageUrl;
    private String matchCode;
}
