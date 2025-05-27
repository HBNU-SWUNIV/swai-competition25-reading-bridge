package com.dyslexia.dyslexia.dto;

import com.dyslexia.dyslexia.enums.Grade;
import com.dyslexia.dyslexia.enums.UserType;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class SignUpRequestDto {
    private String clientId;
    private String name;
    private UserType userType;

    private Grade grade;
    private String type;
    private List<Long> interestIds;

    private String organization;
}