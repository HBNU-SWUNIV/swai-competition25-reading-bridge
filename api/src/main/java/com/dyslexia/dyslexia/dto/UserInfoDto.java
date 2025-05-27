package com.dyslexia.dyslexia.dto;

import com.dyslexia.dyslexia.enums.UserType;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "사용자 정보 응답 DTO")
public interface UserInfoDto {
    @Schema(description = "사용자 ID", example = "1")
    Long getId();

    @Schema(description = "카카오 ID", example = "123456789")
    String getClientId();

    @Schema(description = "사용자 닉네임", example = "홍길동")
    String getName();

    @Schema(description = "사용자 타입", example = "STUDENT")
    UserType getUserType();
} 