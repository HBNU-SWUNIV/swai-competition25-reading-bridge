package com.dyslexia.dyslexia.service;

import com.dyslexia.dyslexia.dto.MatchResponseDto;
import com.dyslexia.dyslexia.entity.Student;
import com.dyslexia.dyslexia.entity.Teacher;
import com.dyslexia.dyslexia.exception.notfound.StudentNotFoundException;
import com.dyslexia.dyslexia.exception.notfound.TeacherNotFoundException;
import com.dyslexia.dyslexia.mapper.TeacherMapper;
import com.dyslexia.dyslexia.repository.StudentRepository;
import com.dyslexia.dyslexia.repository.TeacherRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private TeacherRepository teacherRepository;

    @Mock
    private TeacherMapper teacherMapper;

    @InjectMocks
    private StudentService studentService;

    @Test
    @DisplayName("매칭코드로 학생과 교사 연결 성공")
    void 매칭코드로_학생과_교사_연결_성공() {
        // Given
        Long studentId = 1L;
        String matchCode = "ABC123";

        Teacher teacher = new Teacher("teacher1", "테스트교사", "테스트학교", "http://example.com/profile.jpg");
        ReflectionTestUtils.setField(teacher, "id", 1L);
        ReflectionTestUtils.setField(teacher, "matchCode", matchCode);

        Student student = Student.builder().clientId("student1").build();
        ReflectionTestUtils.setField(student, "id", 1L);

        MatchResponseDto expectedResponse = MatchResponseDto.builder().id(1L)
            .organization("테스트학교")
            .profileImageUrl("http://example.com/profile.jpg")
            .matchCode(matchCode).build();

        when(teacherRepository.findByMatchCode(matchCode)).thenReturn(Optional.of(teacher));
        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(teacherMapper.toMatchResponseDto(teacher)).thenReturn(expectedResponse);

        // When
        MatchResponseDto result = studentService.matchByCode(studentId, matchCode);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getOrganization()).isEqualTo("테스트학교");

        assertThat(teacher.getStudents()).contains(student);
        assertThat(student.getTeacher()).isEqualTo(teacher);

        verify(teacherRepository).findByMatchCode(matchCode);
        verify(studentRepository).findById(studentId);
        verify(teacherMapper).toMatchResponseDto(teacher);
    }

    @Test
    @DisplayName("존재하지 않는 매칭코드로 요청 시 예외 발생")
    void 존재하지_않는_매칭코드로_요청_시_예외_발생() {
        // Given
        Long studentId = 1L;
        String invalidCode = "INVALID";

        when(teacherRepository.findByMatchCode(invalidCode)).thenReturn(Optional.empty());

        // When & Then
        TeacherNotFoundException exception = assertThrows(TeacherNotFoundException.class,
            () -> studentService.matchByCode(studentId, invalidCode));

        assertThat(exception.getMessage()).contains(invalidCode);
        verify(teacherRepository).findByMatchCode(invalidCode);
        verify(studentRepository, never()).findById(any());
    }

    @Test
    @DisplayName("존재하지 않는 학생ID로 요청 시 예외 발생")
    void 존재하지_않는_학생ID로_요청_시_예외_발생() {
        // Given
        Long invalidStudentId = 999L;
        String matchCode = "ABC123";

        Teacher teacher = new Teacher("teacher1", "테스트교사", "테스트학교", "http://example.com/profile.jpg");
        ReflectionTestUtils.setField(teacher, "matchCode", matchCode);

        when(teacherRepository.findByMatchCode(matchCode)).thenReturn(Optional.of(teacher));
        when(studentRepository.findById(invalidStudentId)).thenReturn(Optional.empty());

        // When & Then
        StudentNotFoundException exception = assertThrows(StudentNotFoundException.class,
            () -> studentService.matchByCode(invalidStudentId, matchCode));

        assertThat(exception.getMessage()).contains("학생");
        verify(teacherRepository).findByMatchCode(matchCode);
        verify(studentRepository).findById(invalidStudentId);
    }
}