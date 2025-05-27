package com.dyslexia.dyslexia.mapper;

import com.dyslexia.dyslexia.dto.CourseInfoDto;
import com.dyslexia.dyslexia.entity.CourseInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CourseInfoMapper {

  @Mapping(source = "course.id", target = "courseId")
  @Mapping(source = "student.id", target = "studentId")
  @Mapping(source = "teacher.id", target = "teacherId")
  CourseInfoDto toDto(CourseInfo entity);

}
