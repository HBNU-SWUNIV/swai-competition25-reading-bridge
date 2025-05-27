package com.dyslexia.dyslexia.repository;

import com.dyslexia.dyslexia.entity.Course;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {
  List<Course> findByTeacherId(Long teacherId);
}
