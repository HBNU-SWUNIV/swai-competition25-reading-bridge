package com.dyslexia.dyslexia.dto;

import com.dyslexia.dyslexia.enums.UserType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignUpResponseDto {
    private Long id;
    private String name;
    private UserType userType;
    private String accessToken;
    private String refreshToken;
}