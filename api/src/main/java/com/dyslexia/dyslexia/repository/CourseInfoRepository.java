package com.dyslexia.dyslexia.repository;

import com.dyslexia.dyslexia.entity.CourseInfo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseInfoRepository extends JpaRepository<CourseInfo, Long> {

  List<CourseInfo> findByStudentId(Long studentId);

  List<CourseInfo> findByCourseId(Long courseId);
}
