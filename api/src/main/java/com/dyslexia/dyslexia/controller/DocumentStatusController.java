package com.dyslexia.dyslexia.controller;

import com.dyslexia.dyslexia.dto.DocumentProcessStatusDto;
import com.dyslexia.dyslexia.enums.DocumentProcessStatus;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "DocumentStatus", description = "문서 처리 상태 관리 API")
@RestController
@RequestMapping("documents/status")
@RequiredArgsConstructor
@Slf4j
public class DocumentStatusController {

    private final DocumentProcessService documentProcessService;

    @Operation(summary = "문서 처리 상태 조회", description = "문서의 처리 상태와 진행도를 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공", 
                     content = @Content(schema = @Schema(implementation = DocumentProcessStatusDto.class))),
        @ApiResponse(responseCode = "404", description = "문서를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/{documentId}")
    public ResponseEntity<DocumentProcessStatusDto> getDocumentStatus(
            @Parameter(description = "문서 ID", required = true) 
            @PathVariable("documentId") Long documentId) {
        
        try {
            DocumentProcessStatus status = documentProcessService.getDocumentProcessStatus(documentId);
            int progress = documentProcessService.calculateDocumentProcessProgress(documentId);
            
            DocumentProcessStatusDto responseDto = DocumentProcessStatusDto.builder()
                .success(true)
                .message("문서 처리 상태 조회 성공")
                .documentId(documentId)
                .status(status)
                .progress(progress)
                .build();
            
            return ResponseEntity.ok(responseDto);
            
        } catch (IllegalArgumentException e) {
            log.error("문서 상태 조회 중 오류 발생", e);
            return ResponseEntity.status(404).body(
                DocumentProcessStatusDto.builder()
                    .success(false)
                    .message(e.getMessage())
                    .build()
            );
        } catch (Exception e) {
            log.error("문서 상태 조회 중 오류 발생", e);
            return ResponseEntity.status(500).body(
                DocumentProcessStatusDto.builder()
                    .success(false)
                    .message("문서 상태 조회 중 오류가 발생했습니다: " + e.getMessage())
                    .build()
            );
        }
    }
    
    @Operation(summary = "문서 처리 재시도", description = "실패한 문서의 처리를 재시도합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "재시도 성공", 
                     content = @Content(schema = @Schema(implementation = DocumentProcessStatusDto.class))),
        @ApiResponse(responseCode = "400", description = "재시도할 수 없는 상태"),
        @ApiResponse(responseCode = "404", description = "문서를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/{documentId}/retry")
    public ResponseEntity<DocumentProcessStatusDto> retryDocumentProcessing(
            @Parameter(description = "문서 ID", required = true) 
            @PathVariable("documentId") Long documentId) {
        
        log.info("문서 ID: {}의 처리 재시도 요청", documentId);
        
        try {
            documentProcessService.retryDocumentProcessing(documentId);
            
            DocumentProcessStatusDto responseDto = DocumentProcessStatusDto.builder()
                .success(true)
                .message("문서 처리 재시도가 시작되었습니다.")
                .documentId(documentId)
                .status(DocumentProcessStatus.PENDING)
                .progress(0)
                .build();
            
            return ResponseEntity.ok(responseDto);
            
        } catch (IllegalArgumentException e) {
            log.error("문서 처리 재시도 중 오류 발생", e);
            return ResponseEntity.status(404).body(
                DocumentProcessStatusDto.builder()
                    .success(false)
                    .message(e.getMessage())
                    .build()
            );
        } catch (IllegalStateException e) {
            log.error("문서 처리 재시도 중 오류 발생", e);
            return ResponseEntity.status(400).body(
                DocumentProcessStatusDto.builder()
                    .success(false)
                    .message(e.getMessage())
                    .build()
            );
        } catch (Exception e) {
            log.error("문서 처리 재시도 중 오류 발생", e);
            return ResponseEntity.status(500).body(
                DocumentProcessStatusDto.builder()
                    .success(false)
                    .message("문서 처리 재시도 중 오류가 발생했습니다: " + e.getMessage())
                    .build()
            );
        }
    }
} 