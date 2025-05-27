package com.dyslexia.dyslexia.dto;

import com.dyslexia.dyslexia.entity.Teacher;
import com.dyslexia.dyslexia.enums.UserType;
import lombok.Getter;

@Getter
public class TeacherInfoDto implements UserInfoDto {
    private final Long id;
    private final String clientId;
    private final String name;
    private final UserType userType;

    public TeacherInfoDto(Teacher teacher) {
        this.id = teacher.getId();
        this.clientId = teacher.getClientId();
        this.name = teacher.getName();
        this.userType = UserType.TEACHER;
    }
} 