package com.dyslexia.dyslexia.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.dyslexia.dyslexia.dto.SignUpRequestDto;
import com.dyslexia.dyslexia.dto.SignUpResponseDto;
import com.dyslexia.dyslexia.entity.Interest;
import com.dyslexia.dyslexia.entity.Student;
import com.dyslexia.dyslexia.entity.Teacher;
import com.dyslexia.dyslexia.enums.UserType;
import com.dyslexia.dyslexia.exception.UserAlreadyExistsException;
import com.dyslexia.dyslexia.mapper.StudentMapper;
import com.dyslexia.dyslexia.mapper.TeacherMapper;
import com.dyslexia.dyslexia.repository.InterestRepository;
import com.dyslexia.dyslexia.repository.StudentRepository;
import com.dyslexia.dyslexia.repository.TeacherRepository;
import com.dyslexia.dyslexia.util.JwtTokenProvider;
import java.util.Arrays;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private TeacherRepository teacherRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private InterestRepository interestRepository;

    @Mock
    private TeacherMapper teacherMapper;

    @Mock
    private StudentMapper studentMapper;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private UserService userService;

    private SignUpRequestDto teacherDto;
    private SignUpRequestDto studentDto;

    @BeforeEach
    void setUp() {
        teacherDto = SignUpRequestDto.builder()
            .userType(UserType.TEACHER)
            .clientId("teacher123")
            .name("테스트교사")
            .build();

        studentDto = SignUpRequestDto.builder()
            .userType(UserType.STUDENT)
            .clientId("student123")
            .name("테스트학생")
            .interestIds(List.of(1L, 2L))
            .build();
    }

    @Test
    void 교사_회원가입_성공() {

        //Given
        Teacher teacher = Teacher.builder()
            .clientId("teacher123")
            .name("테스트교사")
            .build();
        ReflectionTestUtils.setField(teacher, "id", 1L);

        when(teacherRepository.existsByClientId(anyString())).thenReturn(false);
        when(teacherMapper.toEntity((SignUpRequestDto) any())).thenReturn(teacher);
        when(teacherRepository.save(any())).thenReturn(teacher);
        when(jwtTokenProvider.createAccessToken(anyString(), anyString())).thenReturn("테스트 액세스토큰");
        when(jwtTokenProvider.createRefreshToken(anyString())).thenReturn("테스트 리프레쉬토큰");

        //When
        SignUpResponseDto response = userService.signUp(teacherDto);

        //Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("테스트교사");
        assertThat(response.getUserType()).isEqualTo(UserType.TEACHER);
        verify(teacherRepository).save(any());
    }

    @Test
    void 학생_회원가입_성공() {

        //Given
        Student student = Student.builder()
            .clientId("student123")
            .name("테스트학생")
            .build();
        ReflectionTestUtils.setField(student, "id", 1L);

        List<Interest> interests = Arrays.asList(
            Interest.builder().name("관심사1").build(),
            Interest.builder().name("관심사2").build()
        );
        ReflectionTestUtils.setField(interests.get(0), "id", 1L);
        ReflectionTestUtils.setField(interests.get(1), "id", 1L);

        when(studentRepository.existsByClientId(anyString())).thenReturn(false);
        when(studentMapper.toEntity(any())).thenReturn(student);
        when(interestRepository.findAllById(anyList())).thenReturn(interests);
        when(studentRepository.save(any())).thenReturn(student);
        when(jwtTokenProvider.createAccessToken(anyString(), anyString())).thenReturn("테스트 액세스토큰");
        when(jwtTokenProvider.createRefreshToken(anyString())).thenReturn("테스트 리프레쉬토큰");

        //When
        SignUpResponseDto response = userService.signUp(studentDto);

        //Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("테스트학생");
        assertThat(response.getUserType()).isEqualTo(UserType.STUDENT);
        verify(studentRepository).save(any());
        verify(interestRepository).findAllById(anyList());
    }

    @Test
    void 교사_회원가입_실패() {

        //Given
        when(teacherRepository.existsByClientId(anyString())).thenReturn(true);

        //When & Then
        UserAlreadyExistsException exception = assertThrows(UserAlreadyExistsException.class,
            () -> userService.signUp(teacherDto));

        Assertions.assertThat(exception.getMessage()).contains("이미 가입된");
    }
}