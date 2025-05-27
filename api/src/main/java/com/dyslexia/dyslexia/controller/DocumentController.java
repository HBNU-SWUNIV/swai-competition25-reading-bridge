package com.dyslexia.dyslexia.controller;

import com.dyslexia.dyslexia.dto.DocumentDto;
import com.dyslexia.dyslexia.dto.DocumentResponseDto;
import com.dyslexia.dyslexia.entity.Document;
import com.dyslexia.dyslexia.enums.DocumentProcessStatus;
import com.dyslexia.dyslexia.enums.Grade;
import com.dyslexia.dyslexia.service.DocumentProcessService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Document", description = "PDF 문서 관리 API")
@RestController
@RequestMapping("documents")
@RequiredArgsConstructor
@Slf4j
public class DocumentController {

    private final DocumentProcessService documentProcessService;

    @Operation(summary = "PDF 문서 업로드", description = "선생님이 PDF 문서를 업로드하고 처리를 시작합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "업로드 성공", 
                     content = @Content(schema = @Schema(implementation = DocumentResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DocumentResponseDto> uploadDocument(
            @Parameter(description = "선생님 ID", required = true) 
            @RequestParam("teacherId") Long teacherId,
            
            @Parameter(description = "PDF 파일", required = true) 
            @RequestParam("file") MultipartFile file,
            
            @Parameter(description = "문서 제목", required = true) 
            @RequestParam("title") String title,
            
            @Parameter(description = "학년", required = true) 
            @RequestParam("grade") Grade grade,
            
            @Parameter(description = "과목 경로", required = false) 
            @RequestParam(value = "subjectPath", required = false) String subjectPath
    ) {
        try {
            log.info("문서 업로드 요청: 선생님 ID: {}, 제목: {}, 학년: {}", teacherId, title, grade);
            
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || !originalFilename.toLowerCase().endsWith(".pdf")) {
                return ResponseEntity.badRequest().body(
                    DocumentResponseDto.builder()
                        .success(false)
                        .message("PDF 파일만 업로드 가능합니다.")
                        .build()
                );
            }
            
            Document document = documentProcessService.uploadDocument(teacherId, file, title, grade, subjectPath);
            
            DocumentResponseDto responseDto = DocumentResponseDto.builder()
                .success(true)
                .message("PDF 업로드 완료. 비동기 처리가 시작되었습니다.")
                .document(mapToDto(document))
                .build();
                
            return ResponseEntity.ok(responseDto);
            
        } catch (IOException e) {
            log.error("파일 업로드 중 오류 발생", e);
            return ResponseEntity.status(500).body(
                DocumentResponseDto.builder()
                    .success(false)
                    .message("파일 업로드 중 오류가 발생했습니다: " + e.getMessage())
                    .build()
            );
        } catch (Exception e) {
            log.error("문서 처리 중 오류 발생", e);
            return ResponseEntity.status(500).body(
                DocumentResponseDto.builder()
                    .success(false)
                    .message("문서 처리 중 오류가 발생했습니다: " + e.getMessage())
                    .build()
            );
        }
    }
    
    private DocumentDto mapToDto(Document document) {
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
    }
} 