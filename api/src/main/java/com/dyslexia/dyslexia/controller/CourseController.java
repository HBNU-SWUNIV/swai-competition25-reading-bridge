package com.dyslexia.dyslexia.controller;

import com.dyslexia.dyslexia.dto.CourseDto;
import com.dyslexia.dyslexia.dto.CourseReqDto;
import com.dyslexia.dyslexia.service.CourseService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/courses")
@RequiredArgsConstructor
public class CourseController {

  private final CourseService courseService;

  @PostMapping
  public ResponseEntity<CourseDto> createCourse(@RequestBody CourseReqDto dto) {
    return ResponseEntity.ok(courseService.saveCourse(dto));
  }

  @GetMapping("/{id}")
  public ResponseEntity<CourseDto> getById(@PathVariable Long id) throws NotFoundException {
    return ResponseEntity.ok(courseService.getById(id));
  }

  @GetMapping
  public ResponseEntity<List<CourseDto>> getCoursesByTeacher(@RequestParam Long teacherId) {
    return ResponseEntity.ok(courseService.getCoursesByTeacher(teacherId));
  }
}