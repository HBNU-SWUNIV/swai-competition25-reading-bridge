package com.dyslexia.dyslexia.controller;

import com.dyslexia.dyslexia.dto.AccessibilitySettingsUpdateRequest;
import com.dyslexia.dyslexia.dto.DocumentDto;
import com.dyslexia.dyslexia.dto.PageDto;
import com.dyslexia.dyslexia.dto.PageListResponseDto;
import com.dyslexia.dyslexia.dto.PageProgressUpdateRequest;
import com.dyslexia.dyslexia.dto.StudentDocumentListResponseDto;
import com.dyslexia.dyslexia.entity.Document;
import com.dyslexia.dyslexia.entity.Page;
import com.dyslexia.dyslexia.entity.StudentDocumentAssignment;
import com.dyslexia.dyslexia.repository.DocumentRepository;
import com.dyslexia.dyslexia.repository.PageRepository;
import com.dyslexia.dyslexia.repository.StudentDocumentAssignmentRepository;
import com.dyslexia.dyslexia.service.StudentDocumentService;
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

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "StudentDocument", description = "학생 문서 관리 API")
@RestController
@RequestMapping("student/documents")
@RequiredArgsConstructor
@Slf4j
public class StudentDocumentController {

    private final StudentDocumentService studentDocumentService;
    private final StudentDocumentAssignmentRepository assignmentRepository;
    private final DocumentRepository documentRepository;
    private final PageRepository pageRepository;

    @Operation(summary = "학생에게 할당된 문서 목록 조회", description = "학생에게 할당된 모든 문서 목록을 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공", 
                     content = @Content(schema = @Schema(implementation = StudentDocumentListResponseDto.class))),
        @ApiResponse(responseCode = "404", description = "학생을 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/{studentId}")
    public ResponseEntity<StudentDocumentListResponseDto> getAssignedDocuments(
            @Parameter(description = "학생 ID", required = true) 
            @PathVariable Long studentId) {
        
        log.info("학생 ID: {}의 할당된 문서 목록 조회 요청", studentId);
        
        try {
            List<StudentDocumentAssignment> assignments = assignmentRepository.findByStudentId(studentId);
            
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
            log.error("학생 문서 목록 조회 중 오류 발생", e);
            return ResponseEntity.status(500).body(
                StudentDocumentListResponseDto.builder()
                    .success(false)
                    .message("문서 목록 조회 중 오류가 발생했습니다: " + e.getMessage())
                    .build()
            );
        }
    }
    
    @Operation(summary = "문서 페이지 목록 조회", description = "특정 문서의 모든 페이지 정보를 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공", 
                     content = @Content(schema = @Schema(implementation = PageListResponseDto.class))),
        @ApiResponse(responseCode = "404", description = "문서를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/{studentId}/document/{documentId}/pages")
    public ResponseEntity<PageListResponseDto> getDocumentPages(
            @Parameter(description = "학생 ID", required = true) 
            @PathVariable Long studentId,
            
            @Parameter(description = "문서 ID", required = true) 
            @PathVariable Long documentId) {
        
        log.info("학생 ID: {}, 문서 ID: {}의 페이지 목록 조회 요청", studentId, documentId);
        
        try {
            // 문서에 대한 학생의 접근 권한 확인
            boolean hasAccess = assignmentRepository.findByStudentIdAndDocumentId(studentId, documentId).isPresent();
            
            if (!hasAccess) {
                return ResponseEntity.status(403).body(
                    PageListResponseDto.builder()
                        .success(false)
                        .message("이 문서에 대한 접근 권한이 없습니다.")
                        .build()
                );
            }
            
            // 문서 존재 여부 확인
            Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new IllegalArgumentException("문서를 찾을 수 없습니다."));
            
            // 페이지 목록 조회
            List<Page> pages = pageRepository.findByDocumentIdOrderByPageNumberAsc(documentId);
            
            if (pages.isEmpty()) {
                return ResponseEntity.ok(
                    PageListResponseDto.builder()
                        .success(true)
                        .message("문서에 페이지가 없습니다.")
                        .pages(List.of())
                        .build()
                );
            }
            
            List<PageDto> pageDtos = pages.stream()
                .map(page -> PageDto.builder()
                    .id(page.getId())
                    .documentId(page.getDocument().getId())
                    .pageNumber(page.getPageNumber())
                    .sectionTitle(page.getSectionTitle())
                    .readingLevel(page.getReadingLevel())
                    .wordCount(page.getWordCount())
                    .complexityScore(page.getComplexityScore())
                    .processingStatus(page.getProcessingStatus())
                    .build())
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(
                PageListResponseDto.builder()
                    .success(true)
                    .message("페이지 목록 조회 성공")
                    .pages(pageDtos)
                    .build()
            );
            
        } catch (IllegalArgumentException e) {
            log.error("문서 페이지 목록 조회 중 오류 발생", e);
            return ResponseEntity.status(404).body(
                PageListResponseDto.builder()
                    .success(false)
                    .message(e.getMessage())
                    .build()
            );
        } catch (Exception e) {
            log.error("문서 페이지 목록 조회 중 오류 발생", e);
            return ResponseEntity.status(500).body(
                PageListResponseDto.builder()
                    .success(false)
                    .message("페이지 목록 조회 중 오류가 발생했습니다: " + e.getMessage())
                    .build()
            );
        }
    }
    
    @Operation(summary = "페이지 상세 조회", description = "특정 페이지의 상세 내용과 팁, 이미지를 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "404", description = "페이지를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/{studentId}/page/{pageId}")
    public ResponseEntity<?> getPageDetail(
            @Parameter(description = "학생 ID", required = true) 
            @PathVariable Long studentId,
            
            @Parameter(description = "페이지 ID", required = true) 
            @PathVariable Long pageId) {
        
        log.info("학생 ID: {}, 페이지 ID: {}의 상세 내용 조회 요청", studentId, pageId);
        
        try {
            return studentDocumentService.getPageDetail(studentId, pageId);
        } catch (Exception e) {
            log.error("페이지 상세 조회 중 오류 발생", e);
            return ResponseEntity.status(500).body(
                PageListResponseDto.builder()
                    .success(false)
                    .message("페이지 상세 조회 중 오류가 발생했습니다: " + e.getMessage())
                    .build()
            );
        }
    }
    
    @Operation(summary = "페이지 진행 상태 업데이트", description = "학생의 페이지 학습 진행 상태를 업데이트합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "업데이트 성공"),
        @ApiResponse(responseCode = "404", description = "페이지를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/{studentId}/page/{pageId}/progress")
    public ResponseEntity<?> updatePageProgress(
            @Parameter(description = "학생 ID", required = true) 
            @PathVariable Long studentId,
            
            @Parameter(description = "페이지 ID", required = true) 
            @PathVariable Long pageId,
            
            @RequestBody PageProgressUpdateRequest request) {
        
        log.info("학생 ID: {}, 페이지 ID: {}의 진행 상태 업데이트 요청", studentId, pageId);
        
        try {
            return studentDocumentService.updatePageProgress(studentId, pageId, request);
        } catch (Exception e) {
            log.error("페이지 진행 상태 업데이트 중 오류 발생", e);
            return ResponseEntity.status(500).body(
                PageListResponseDto.builder()
                    .success(false)
                    .message("페이지 진행 상태 업데이트 중 오류가 발생했습니다: " + e.getMessage())
                    .build()
            );
        }
    }
    
    @Operation(summary = "페이지 접근성 설정 업데이트", description = "학생의 페이지 접근성 설정을 업데이트합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "업데이트 성공"),
        @ApiResponse(responseCode = "404", description = "페이지를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/{studentId}/page/{pageId}/accessibility")
    public ResponseEntity<?> updateAccessibilitySettings(
            @Parameter(description = "학생 ID", required = true) 
            @PathVariable Long studentId,
            
            @Parameter(description = "페이지 ID", required = true) 
            @PathVariable Long pageId,
            
            @RequestBody AccessibilitySettingsUpdateRequest request) {
        
        log.info("학생 ID: {}, 페이지 ID: {}의 접근성 설정 업데이트 요청", studentId, pageId);
        
        try {
            return studentDocumentService.updateAccessibilitySettings(studentId, pageId, request);
        } catch (Exception e) {
            log.error("페이지 접근성 설정 업데이트 중 오류 발생", e);
            return ResponseEntity.status(500).body(
                PageListResponseDto.builder()
                    .success(false)
                    .message("페이지 접근성 설정 업데이트 중 오류가 발생했습니다: " + e.getMessage())
                    .build()
            );
        }
    }
} 