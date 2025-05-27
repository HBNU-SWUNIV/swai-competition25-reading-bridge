package com.dyslexia.dyslexia.controller;

import com.dyslexia.dyslexia.dto.DocumentAssignmentRequest;
import com.dyslexia.dyslexia.dto.DocumentDto;
import com.dyslexia.dyslexia.dto.ResponseDto;
import com.dyslexia.dyslexia.dto.StudentDocumentListResponseDto;
import com.dyslexia.dyslexia.entity.Document;
import com.dyslexia.dyslexia.entity.Student;
import com.dyslexia.dyslexia.entity.StudentDocumentAssignment;
import com.dyslexia.dyslexia.entity.Teacher;
import com.dyslexia.dyslexia.repository.DocumentRepository;
import com.dyslexia.dyslexia.repository.StudentDocumentAssignmentRepository;
import com.dyslexia.dyslexia.repository.StudentRepository;
import com.dyslexia.dyslexia.repository.TeacherRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "TeacherDocument", description = "선생님 문서 관리 API")
@RestController
@RequestMapping("teacher/documents")
@RequiredArgsConstructor
@Slf4j
public class TeacherDocumentController {

    private final DocumentRepository documentRepository;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;
    private final StudentDocumentAssignmentRepository assignmentRepository;

    @Operation(summary = "선생님이 업로드한 문서 목록 조회", description = "선생님이 업로드한 모든 문서 목록을 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공", 
                     content = @Content(schema = @Schema(implementation = StudentDocumentListResponseDto.class))),
        @ApiResponse(responseCode = "404", description = "선생님을 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/{teacherId}")
    public ResponseEntity<StudentDocumentListResponseDto> getTeacherDocuments(
            @Parameter(description = "선생님 ID", required = true) 
            @PathVariable("teacherId") Long teacherId) {
        
        log.info("선생님 ID: {}의 문서 목록 조회 요청", teacherId);
        
        try {
            List<Document> documents = documentRepository.findAllByOrderByCreatedAtDesc();
            
            if (documents.isEmpty()) {
                return ResponseEntity.ok(
                    StudentDocumentListResponseDto.builder()
                        .success(true)
                        .message("업로드한 문서가 없습니다.")
                        .documents(List.of())
                        .build()
                );
            }
            
            List<DocumentDto> documentDtos = documents.stream()
                .map(document -> DocumentDto.builder()
                    .id(document.getId())
                    .teacherId(document.getTeacher().getId())
                    .title(document.getTitle())
                    .originalFilename(document.getOriginalFilename())
                    .fileSize(document.getFileSize())
                    .pageCount(document.getPageCount())
                    .grade(document.getGrade())
                    .subjectPath(document.getSubjectPath())
                    .processStatus(document.getProcessStatus())
                    .createdAt(document.getCreatedAt())
                    .updatedAt(document.getUpdatedAt())
                    .build())
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(
                StudentDocumentListResponseDto.builder()
                    .success(true)
                    .message("문서 목록 조회 성공")
                    .documents(documentDtos)
                    .build()
            );
            
        } catch (Exception e) {
            log.error("선생님 문서 목록 조회 중 오류 발생", e);
            return ResponseEntity.status(500).body(
                StudentDocumentListResponseDto.builder()
                    .success(false)
                    .message("문서 목록 조회 중 오류가 발생했습니다: " + e.getMessage())
                    .build()
            );
        }
    }
    
    @Operation(summary = "학생에게 문서 할당", description = "선생님이 학생에게 문서를 할당합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "할당 성공", 
                     content = @Content(schema = @Schema(implementation = ResponseDto.class))),
        @ApiResponse(responseCode = "404", description = "선생님, 학생 또는 문서를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/assign")
    public ResponseEntity<ResponseDto> assignDocumentToStudent(
            @RequestBody DocumentAssignmentRequest request) {
        
        log.info("문서 할당 요청: 선생님 ID: {}, 학생 ID: {}, 문서 ID: {}", 
                request.getTeacherId(), request.getStudentId(), request.getDocumentId());
        
        try {
            // 선생님 존재 여부 확인
            Teacher teacher = teacherRepository.findById(request.getTeacherId())
                .orElseThrow(() -> new IllegalArgumentException("선생님을 찾을 수 없습니다."));
            
            // 학생 존재 여부 확인
            Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new IllegalArgumentException("학생을 찾을 수 없습니다."));
            
            // 문서 존재 여부 확인
            Document document = documentRepository.findById(request.getDocumentId())
                .orElseThrow(() -> new IllegalArgumentException("문서를 찾을 수 없습니다."));
            
            // 이미 할당되었는지 확인
            boolean alreadyAssigned = assignmentRepository.findByStudentIdAndDocumentId(
                    request.getStudentId(), request.getDocumentId()).isPresent();
            
            if (alreadyAssigned) {
                return ResponseEntity.ok(
                    ResponseDto.builder()
                        .success(true)
                        .message("이미 학생에게 할당된 문서입니다.")
                        .build()
                );
            }
            
            // 할당 정보 생성
            StudentDocumentAssignment assignment = StudentDocumentAssignment.builder()
                .student(student)
                .document(document)
                .assignedBy(teacher)
                .assignedAt(LocalDateTime.now())
                .dueDate(request.getDueDate())
                .notes(request.getNotes())
                .build();
            
            // 저장
            assignmentRepository.save(assignment);
            
            return ResponseEntity.ok(
                ResponseDto.builder()
                    .success(true)
                    .message("문서가 학생에게 할당되었습니다.")
                    .build()
            );
            
        } catch (IllegalArgumentException e) {
            log.error("문서 할당 중 오류 발생", e);
            return ResponseEntity.status(404).body(
                ResponseDto.builder()
                    .success(false)
                    .message(e.getMessage())
                    .build()
            );
        } catch (Exception e) {
            log.error("문서 할당 중 오류 발생", e);
            return ResponseEntity.status(500).body(
                ResponseDto.builder()
                    .success(false)
                    .message("문서 할당 중 오류가 발생했습니다: " + e.getMessage())
                    .build()
            );
        }
    }
    
    @Operation(summary = "학생 문서 할당 취소", description = "선생님이 학생에게 할당한 문서를 취소합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "취소 성공", 
                     content = @Content(schema = @Schema(implementation = ResponseDto.class))),
        @ApiResponse(responseCode = "404", description = "할당 정보를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @DeleteMapping("/assign/{teacherId}/{studentId}/{documentId}")
    public ResponseEntity<ResponseDto> unassignDocumentFromStudent(
            @Parameter(description = "선생님 ID", required = true) 
            @PathVariable Long teacherId,
            
            @Parameter(description = "학생 ID", required = true) 
            @PathVariable Long studentId,
            
            @Parameter(description = "문서 ID", required = true) 
            @PathVariable Long documentId) {
        
        log.info("문서 할당 취소 요청: 선생님 ID: {}, 학생 ID: {}, 문서 ID: {}", 
                teacherId, studentId, documentId);
        
        try {
            // 할당 정보 조회
            StudentDocumentAssignment assignment = assignmentRepository
                .findByStudentIdAndDocumentId(studentId, documentId)
                .orElseThrow(() -> new IllegalArgumentException("할당된 문서를 찾을 수 없습니다."));
            
            // 할당한 선생님인지 확인
            if (!assignment.getTeacher().getId().equals(teacherId)) {
                return ResponseEntity.status(403).body(
                    ResponseDto.builder()
                        .success(false)
                        .message("이 문서를 할당한 선생님만 취소할 수 있습니다.")
                        .build()
                );
            }
            
            // 삭제
            assignmentRepository.delete(assignment);
            
            return ResponseEntity.ok(
                ResponseDto.builder()
                    .success(true)
                    .message("문서 할당이 취소되었습니다.")
                    .build()
            );
            
        } catch (IllegalArgumentException e) {
            log.error("문서 할당 취소 중 오류 발생", e);
            return ResponseEntity.status(404).body(
                ResponseDto.builder()
                    .success(false)
                    .message(e.getMessage())
                    .build()
            );
        } catch (Exception e) {
            log.error("문서 할당 취소 중 오류 발생", e);
            return ResponseEntity.status(500).body(
                ResponseDto.builder()
                    .success(false)
                    .message("문서 할당 취소 중 오류가 발생했습니다: " + e.getMessage())
                    .build()
            );
        }
    }
    
    @Operation(summary = "선생님의 학생별 할당 문서 목록 조회", description = "선생님이 특정 학생에게 할당한 문서 목록을 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공", 
                     content = @Content(schema = @Schema(implementation = StudentDocumentListResponseDto.class))),
        @ApiResponse(responseCode = "404", description = "선생님 또는 학생을 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/{teacherId}/student/{studentId}")
    public ResponseEntity<StudentDocumentListResponseDto> getAssignedDocumentsForStudent(
            @Parameter(description = "선생님 ID", required = true) 
            @PathVariable Long teacherId,
            
            @Parameter(description = "학생 ID", required = true) 
            @PathVariable Long studentId) {
        
        log.info("선생님 ID: {}, 학생 ID: {}의 할당된 문서 목록 조회 요청", teacherId, studentId);
        
        try {
            List<StudentDocumentAssignment> assignments = assignmentRepository
                .findByAssignedByIdAndStudentId(teacherId, studentId);
            
            if (assignments.isEmpty()) {
                return ResponseEntity.ok(
                    StudentDocumentListResponseDto.builder()
                        .success(true)
                        .message("할당된 문서가 없습니다.")
                        .documents(List.of())
                        .build()
                );
            }
            
            List<DocumentDto> documents = assignments.stream()
                .map(assignment -> {
                    Document document = assignment.getDocument();
                    return DocumentDto.builder()
                        .id(document.getId())
                        .teacherId(document.getTeacher().getId())
                        .title(document.getTitle())
                        .originalFilename(document.getOriginalFilename())
                        .fileSize(document.getFileSize())
                        .pageCount(document.getPageCount())
                        .grade(document.getGrade())
                        .subjectPath(document.getSubjectPath())
                        .processStatus(document.getProcessStatus())
                        .createdAt(document.getCreatedAt())
                        .updatedAt(document.getUpdatedAt())
                        .build();
                })
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(
                StudentDocumentListResponseDto.builder()
                    .success(true)
                    .message("할당된 문서 목록 조회 성공")
                    .documents(documents)
                    .build()
            );
            
        } catch (Exception e) {
            log.error("할당된 문서 목록 조회 중 오류 발생", e);
            return ResponseEntity.status(500).body(
                StudentDocumentListResponseDto.builder()
                    .success(false)
                    .message("할당된 문서 목록 조회 중 오류가 발생했습니다: " + e.getMessage())
                    .build()
            );
        }
    }
} 