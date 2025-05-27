package com.dyslexia.dyslexia.dto;

import com.dyslexia.dyslexia.entity.Student;
import com.dyslexia.dyslexia.enums.UserType;
import lombok.Getter;

@Getter
public class StudentInfoDto implements UserInfoDto {
    private final Long id;
    private final String clientId;
    private final String name;
    private final UserType userType;

    public StudentInfoDto(Student student) {
        this.id = student.getId();
        this.clientId = student.getClientId();
        this.name = student.getName();
        this.userType = UserType.STUDENT;
    }
} 