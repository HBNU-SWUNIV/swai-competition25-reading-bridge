package com.dyslexia.dyslexia.controller;

import com.dyslexia.dyslexia.dto.CourseInfoDto;
import com.dyslexia.dyslexia.dto.CourseInfoReqDto;
import com.dyslexia.dyslexia.service.CourseInfoService;
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
@RequestMapping("/courseInfo")
@RequiredArgsConstructor
public class CourseInfoController {

  private final CourseInfoService courseInfoService;

  @PostMapping
  public ResponseEntity<CourseInfoDto> createCourseInfo(@RequestBody CourseInfoReqDto dto) {
    return ResponseEntity.ok(courseInfoService.saveCourseInfo(dto));
  }

  @GetMapping("/{id}")
  public ResponseEntity<CourseInfoDto> getById(@PathVariable Long id) throws NotFoundException {
    return ResponseEntity.ok(courseInfoService.getById(id));
  }

  @GetMapping
  public ResponseEntity<List<CourseInfoDto>> getByStudent(@RequestParam Long studentId) {
    return ResponseEntity.ok(courseInfoService.getInfosByStudent(studentId));
  }
}
