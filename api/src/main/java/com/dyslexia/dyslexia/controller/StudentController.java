package com.dyslexia.dyslexia.controller;

import com.dyslexia.dyslexia.dto.MatchResponseDto;
import com.dyslexia.dyslexia.dto.StudentDto;
import com.dyslexia.dyslexia.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Student", description = "학생 관련 API")
@RestController
@RequestMapping("/students")
@RequiredArgsConstructor
public class StudentController {

  private final StudentService studentService;

  @Operation(summary = "id로 학생 조회", description = "학생 ID를 통해 학생 정보를 조회합니다.")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "조회 성공",
        content = @Content(schema = @Schema(implementation = StudentDto.class))),
  })
  @GetMapping("/{id}")
  public ResponseEntity<StudentDto> getById(
      @Parameter(description = "학생 ID", required = true) @PathVariable Long id) {
    return ResponseEntity.ok(studentService.getById(id));
  }

  @Operation(summary = "client id로 학생 조회", description = "클라이언트 ID를 통해 학생 정보를 조회합니다.")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "조회 성공",
        content = @Content(schema = @Schema(implementation = StudentDto.class))),
  })
  @GetMapping
  public ResponseEntity<StudentDto> getByClientId(
      @Parameter(description = "클라이언트 ID", required = true) @RequestParam String clientId) {
    return ResponseEntity.ok(studentService.getByClientId(clientId));
  }

  @Operation(summary = "매칭 코드로 교사 매칭", description = "매칭 코드를 사용하여 학생과 교사를 연결합니다.")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "매칭 성공",
        content = @Content(schema = @Schema(implementation = MatchResponseDto.class))),
  })
  @PostMapping("/match/{id}")
  public ResponseEntity<MatchResponseDto> matchByCode(
      @Parameter(description = "학생 ID", required = true) @PathVariable Long id,
      @Parameter(description = "매칭 코드", required = true) @RequestParam String code) {
    return ResponseEntity.ok(studentService.matchByCode(id, code));
  }
}
