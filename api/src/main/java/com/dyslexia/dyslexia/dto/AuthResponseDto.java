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
public class AuthResponseDto {
    private boolean registered;
    private String clientId;
    private String nickname;
    private String userType;
    private String accessToken;
    private String refreshToken;
}