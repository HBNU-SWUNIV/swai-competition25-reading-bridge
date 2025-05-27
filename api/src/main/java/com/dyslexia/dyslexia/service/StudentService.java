package com.dyslexia.dyslexia.service;

import com.dyslexia.dyslexia.dto.MatchResponseDto;
import com.dyslexia.dyslexia.dto.StudentDto;
import com.dyslexia.dyslexia.dto.TeacherDto;
import com.dyslexia.dyslexia.entity.Student;
import com.dyslexia.dyslexia.entity.Teacher;
import com.dyslexia.dyslexia.exception.notfound.StudentNotFoundException;
import com.dyslexia.dyslexia.exception.notfound.TeacherNotFoundException;
import com.dyslexia.dyslexia.mapper.StudentMapper;
import com.dyslexia.dyslexia.mapper.TeacherMapper;
import com.dyslexia.dyslexia.repository.StudentRepository;
import com.dyslexia.dyslexia.repository.TeacherRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudentService {

  private final StudentRepository studentRepository;
  private final TeacherRepository teacherRepository;
  private final StudentMapper studentMapper;
  private final TeacherMapper teacherMapper;

  public StudentDto getById(Long id) {
    Student student = studentRepository.findById(id)
        .orElseThrow(() -> new StudentNotFoundException("아이디 '" + id + "'에 해당하는 학생을 찾을 수 없습니다."));

    return studentMapper.toDto(student);
  }

  public List<StudentDto> getStudentsByTeacher(Long teacherId) {
    return studentRepository.findByTeacherId(teacherId).stream().map(studentMapper::toDto).toList();
  }

  @Transactional
  public MatchResponseDto matchByCode(Long id, String code) {
    Teacher teacher = teacherRepository.findByMatchCode(code)
        .orElseThrow(() -> new TeacherNotFoundException("코드 '" + code + "'에 해당하는 교사를 찾을 수 없습니다."));

    Student student = studentRepository.findById(id)
        .orElseThrow(() -> new StudentNotFoundException("학생을 찾을 수 없습니다."));

    teacher.addStudent(student);
    return teacherMapper.toMatchResponseDto(teacher);
  }

  public StudentDto getByClientId(String clientId) {
    Student student = studentRepository.findByClientId(clientId)
        .orElseThrow(() -> new TeacherNotFoundException("클라이언트 '" + clientId + "'에 해당하는 학생을 찾을 수 없습니다."));

    return studentMapper.toDto(student);
  }

}