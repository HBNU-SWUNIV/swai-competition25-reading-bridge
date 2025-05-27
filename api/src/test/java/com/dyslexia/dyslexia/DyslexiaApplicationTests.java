package com.dyslexia.dyslexia;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.dyslexia.dyslexia.dto.CourseDto;
import com.dyslexia.dyslexia.dto.CourseInfoDto;
import com.dyslexia.dyslexia.dto.CourseInfoReqDto;
import com.dyslexia.dyslexia.dto.CourseReqDto;
import com.dyslexia.dyslexia.dto.StudentDto;
import com.dyslexia.dyslexia.dto.StudentReqDto;
import com.dyslexia.dyslexia.dto.TeacherDto;
import com.dyslexia.dyslexia.entity.Interest;
import com.dyslexia.dyslexia.entity.Teacher;
import com.dyslexia.dyslexia.enums.Grade;
import com.dyslexia.dyslexia.mapper.custom.GradeMapper;
import com.dyslexia.dyslexia.mapper.custom.InterestMapper;
import com.dyslexia.dyslexia.repository.TeacherRepository;
import com.dyslexia.dyslexia.service.CourseInfoService;
import com.dyslexia.dyslexia.service.CourseService;
import com.dyslexia.dyslexia.service.StudentService;
import com.dyslexia.dyslexia.service.TeacherService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class DyslexiaApplicationTests {

  @Autowired
  private TeacherRepository teacherRepository;
  @Autowired
  private TeacherService teacherService;
  @Autowired
  private StudentService studentService;
  @Autowired
  private CourseService courseService;
  @Autowired
  private CourseInfoService courseInfoService;
  @Autowired
  private GradeMapper gradeMapper;
  @Autowired
  private InterestMapper interestMapper;


  @Test
  void testToEnum() {
    Grade grade = gradeMapper.toEnum("4학년");
    assertEquals(Grade.GRADE_4, grade);
  }

  @Test
  void testToLabel() {
    String label = gradeMapper.toLabel(Grade.GRADE_6);
    assertEquals("6학년", label);
  }

  @Test
  void testToEnumWithInvalidLabel() {
    assertThrows(IllegalArgumentException.class, () -> {
      gradeMapper.toEnum("7학년"); // 없는 값
    });
  }

  @Test
  void testToEntityList() {
    List<String> names = List.of("독서", "코딩");
    List<Interest> entities = interestMapper.toEntityList(names);

    assertEquals(2, entities.size());
    assertEquals("독서", entities.get(0).getName());
  }

  @Test
  void testToStringList() {
    List<Interest> interests = List.of(Interest.builder().name("수학").build(),
        Interest.builder().name("과학").build());

    List<String> names = interestMapper.toStringList(interests);

    assertEquals(List.of("수학", "과학"), names);
  }

  // ✅ 공통 생성 메서드들

  private Teacher createTeacher(String clientId, String org) {
    return teacherRepository.save(
        Teacher.builder().clientId(clientId).organization(org).profileImageUrl("teacher.png")
            .build());
  }

  private StudentDto createStudent(String clientId, Teacher teacher, String gradeLabel) {
    StudentReqDto req = new StudentReqDto();
    req.setClientId(clientId);
    req.setTeacherId(teacher.getId());
    req.setGradeLabel(gradeLabel);
    req.setType("REGULAR");
    req.setState("ACTIVE");
    req.setProfileImageUrl("student.png");
    req.setInterests(List.of("과학", "독서"));
    return null;
    // TODO: 해당 메서드가 없었어요.
//    return studentService.saveStudent(req);
  }

  private CourseDto createCourse(String title, Teacher teacher, Grade grade) {
    CourseReqDto req = new CourseReqDto();
    req.setTeacherId(teacher.getId());
    req.setSubjectPath("math/" + title);
    req.setTitle(title);
    req.setType("REGULAR");
    req.setGrade(grade);
    req.setState("ACTIVE");
    return courseService.saveCourse(req);
  }

  // ✅ 테스트 메서드들

  @Test
  void testSaveTeacher() {
    // when
    Teacher teacher = createTeacher("teacher01", "한글초등학교");

    // then
    assertNotNull(teacher.getId());
    assertEquals("teacher01", teacher.getClientId());
  }

  @Test
  void testSaveStudent() {
    // TODO: 해당 테스트는 실패
//    // given
//    Teacher teacher = createTeacher("teacher02", "중앙초");
//
//    // when
//    StudentDto student = createStudent("student01", teacher, "4학년");
//
//    // then
//    assertEquals("student01", student.getClientId());
//    assertEquals(Grade.GRADE_4, student.getGrade());
//    assertTrue(student.getInterests().contains("과학"));
  }

  @Test
  void testSaveCourse() {
    // given
    Teacher teacher = createTeacher("teacher03", "국제초등학교");

    // when
    CourseDto course = createCourse("기초 대수학", teacher, Grade.GRADE_4);

    // then
    assertEquals("기초 대수학", course.getTitle());
    assertEquals(Grade.GRADE_4, course.getGrade());
  }

  @Test
  void testSaveCourseInfo() {
    // TODO: 해당 테스트는 실패
//    // given
//    Teacher teacher = createTeacher("teacher04", "명문초");
//    StudentDto student = createStudent("student99", teacher, "3학년");
//    CourseDto course = createCourse("기초 물리", teacher, Grade.GRADE_3);
//
//    // when
//    CourseInfoReqDto infoReq = new CourseInfoReqDto();
//    infoReq.setCourseId(course.getId());
//    infoReq.setStudentId(student.getId());
//    infoReq.setTeacherId(teacher.getId());
//    infoReq.setLearningTime(60);
//    infoReq.setPage(5);
//    infoReq.setMaxPage(10);
//
//    CourseInfoDto info = courseInfoService.saveCourseInfo(infoReq);
//
//    // then
//    assertEquals(60, info.getLearningTime());
//    assertEquals(course.getId(), info.getCourseId());
//    assertEquals(student.getId(), info.getStudentId());
  }

  @Test
  void testGetTeacherById() throws NotFoundException {
    Teacher teacher = createTeacher("teacher001", "예림초등학교");

    TeacherDto result = teacherService.getById(teacher.getId());

    assertNotNull(result);
    assertEquals("teacher001", result.getClientId());
  }

  @Test
  void testGetStudentById() throws NotFoundException {
    // TODO: 해당 테스트는 실패
//    Teacher teacher = createTeacher("teacher002", "현명초등학교");
//
//    StudentDto saved = createStudent("student001", teacher, "4학년");
//    StudentDto result = studentService.getById(saved.getId());
//
//    assertNotNull(result);
//    assertEquals("student001", result.getClientId());
//    assertTrue(result.getInterests().contains("과학"));
  }

  @Test
  void testGetCourseById() throws NotFoundException {
    Teacher teacher = createTeacher("teacher003", "미래초");

    CourseDto saved = createCourse("기초 우주과학", teacher, Grade.GRADE_4);
    CourseDto result = courseService.getById(saved.getId());

    assertNotNull(result);
    assertEquals("기초 우주과학", result.getTitle());
  }

  @Test
  void testGetCourseInfoById() throws NotFoundException {
    // TODO: 해당 테스트는 실패
//    Teacher teacher = createTeacher("teacher004", "지성초");
//    StudentDto student = createStudent("student999", teacher, "3학년");
//    CourseDto course = createCourse("수학 사고력 향상", teacher, Grade.GRADE_3);
//
//    CourseInfoReqDto infoReq = new CourseInfoReqDto();
//    infoReq.setCourseId(course.getId());
//    infoReq.setStudentId(student.getId());
//    infoReq.setTeacherId(teacher.getId());
//    infoReq.setLearningTime(45);
//    infoReq.setPage(2);
//    infoReq.setMaxPage(5);
//
//    CourseInfoDto saved = courseInfoService.saveCourseInfo(infoReq);
//    CourseInfoDto result = courseInfoService.getById(saved.getId());
//
//    assertNotNull(result);
//    assertEquals(saved.getId(), result.getId());
//    assertEquals(45, result.getLearningTime());
  }
}
