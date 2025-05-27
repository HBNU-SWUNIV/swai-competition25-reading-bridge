package com.dyslexia.dyslexia.service;

import com.dyslexia.dyslexia.dto.CourseDto;
import com.dyslexia.dyslexia.dto.CourseReqDto;
import com.dyslexia.dyslexia.entity.Course;
import com.dyslexia.dyslexia.entity.Teacher;
import com.dyslexia.dyslexia.mapper.CourseMapper;
import com.dyslexia.dyslexia.repository.CourseRepository;
import com.dyslexia.dyslexia.repository.TeacherRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CourseService {

  private final CourseRepository courseRepository;
  private final TeacherRepository teacherRepository;
  private final CourseMapper courseMapper;

  @Transactional
  public CourseDto saveCourse(CourseReqDto dto) {
    Teacher teacher = teacherRepository.findById(dto.getTeacherId())
        .orElseThrow(() -> new IllegalArgumentException("Teacher not found"));

    Course course = Course.builder().teacher(teacher).subjectPath(dto.getSubjectPath())
        .title(dto.getTitle()).type(dto.getType()).grade(dto.getGrade()).state(dto.getState())
        .build();

    return courseMapper.toDto(courseRepository.save(course));
  }

  public CourseDto getById(Long id) throws NotFoundException {
    return courseRepository.findById(id).map(courseMapper::toDto)
        .orElseThrow(NotFoundException::new);
  }

  public List<CourseDto> getCoursesByTeacher(Long teacherId) {
    return courseRepository.findByTeacherId(teacherId).stream().map(courseMapper::toDto).toList();
  }


}
