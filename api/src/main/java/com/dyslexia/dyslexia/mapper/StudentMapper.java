package com.dyslexia.dyslexia.mapper;

import com.dyslexia.dyslexia.dto.SignUpRequestDto;
import com.dyslexia.dyslexia.dto.StudentDto;
import com.dyslexia.dyslexia.entity.Student;
import com.dyslexia.dyslexia.mapper.custom.GradeMapper;
import com.dyslexia.dyslexia.mapper.custom.InterestMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {GradeMapper.class, InterestMapper.class})
public interface StudentMapper {

  @Mapping(source = "teacher.id", target = "teacherId")
  @Mapping(source = "grade", target = "grade")
  @Mapping(source = "interests", target = "interests")
  StudentDto toDto(Student entity);

  @Mapping(target = "teacher", ignore = true)
  @Mapping(target = "state", ignore = true)
  @Mapping(target = "profileImageUrl", ignore = true)
  @Mapping(target = "interests", ignore = true)
  Student toEntity(SignUpRequestDto dto);

}
