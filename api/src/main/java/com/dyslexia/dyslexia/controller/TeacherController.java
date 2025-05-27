package com.dyslexia.dyslexia.controller;

import com.dyslexia.dyslexia.dto.StudentDto;
import com.dyslexia.dyslexia.dto.TeacherCodeDto;
import com.dyslexia.dyslexia.dto.TeacherDto;
import com.dyslexia.dyslexia.service.StudentService;
import com.dyslexia.dyslexia.service.TeacherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Teacher", description = "교사 관련 API")
@RestController
@RequestMapping("/teachers")
@RequiredArgsConstructor
public class TeacherController {

  private final TeacherService teacherService;
  private final StudentService studentService;

  @Operation(summary = "id로 교사 조회", description = "교사 ID를 통해 교사 정보를 조회합니다.")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "조회 성공",
      content = @Content(schema = @Schema(implementation = TeacherDto.class))),
  })
  @GetMapping("/{id}")
  public ResponseEntity<TeacherDto> getById(
      @Parameter(description = "교사 ID", required = true) @PathVariable long id) {
    return ResponseEntity.ok(teacherService.getById(id));
  }

  @Operation(summary = "client id로 교사 조회", description = "클라이언트 ID를 통해 교사 정보를 조회합니다.")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "조회 성공",
      content = @Content(schema = @Schema(implementation = TeacherDto.class))),
  })

  @GetMapping
  public ResponseEntity<TeacherDto> getByClientId(
      @Parameter(description = "클라이언트 ID", required = true) @RequestParam String clientId) {
    return ResponseEntity.ok(teacherService.getTeacherByClientId(clientId));
  }

  @Operation(summary = "id로 매칭 코드 조회", description = "교사 ID를 통해 매칭 코드를 조회합니다.")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "조회 성공",
      content = @Content(schema = @Schema(implementation = TeacherCodeDto.class))),
  })
  @GetMapping("/code/{id}")
  public ResponseEntity<TeacherCodeDto> getCodeById(
      @Parameter(description = "교사 ID", required = true) @PathVariable long id)
      throws NotFoundException {
    return ResponseEntity.ok(teacherService.getCodeById(id));
  }

  @Operation(summary = "id로 담당 학생 조회", description = "교사 ID를 통해 담당 학생 목록을 조회합니다.")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "조회 성공",
      content = @Content(schema = @Schema(implementation = StudentDto.class))),
  })
  @GetMapping("/{teacherId}/students")
  public ResponseEntity<List<StudentDto>> getStudentsByTeacherId(
      @Parameter(description = "교사 ID", required = true) @PathVariable Long teacherId) {
    return ResponseEntity.ok(studentService.getStudentsByTeacher(teacherId));
  }
}
