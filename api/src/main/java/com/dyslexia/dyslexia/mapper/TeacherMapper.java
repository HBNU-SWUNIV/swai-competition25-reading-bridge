package com.dyslexia.dyslexia.mapper;

import com.dyslexia.dyslexia.dto.MatchResponseDto;
import com.dyslexia.dyslexia.dto.SignUpRequestDto;
import com.dyslexia.dyslexia.dto.TeacherCodeDto;
import com.dyslexia.dyslexia.dto.TeacherDto;
import com.dyslexia.dyslexia.entity.Teacher;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TeacherMapper {

  Teacher toEntity(TeacherDto dto);

  @Mapping(target = "profileImageUrl", ignore = true)
  Teacher toEntity(SignUpRequestDto dto);

  TeacherDto toDto(Teacher entity);

  @Mapping(target = "matchCode", source = "matchCode")
  TeacherCodeDto toCodeDto(Teacher teacher);

  MatchResponseDto toMatchResponseDto(Teacher teacher);
}