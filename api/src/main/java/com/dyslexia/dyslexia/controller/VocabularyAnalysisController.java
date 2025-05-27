package com.dyslexia.dyslexia.controller;

import com.dyslexia.dyslexia.domain.pdf.VocabularyAnalysis;
import com.dyslexia.dyslexia.dto.VocabularyAnalysisSearchRequest;
import com.dyslexia.dyslexia.service.VocabularyAnalysisService;
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

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("vocabulary-analysis")
@Tag(name = "어휘 분석", description = "문서 내 어휘 분석 결과 조회 API")
public class VocabularyAnalysisController {

    private final VocabularyAnalysisService vocabularyAnalysisService;

    @Operation(
        summary = "어휘 분석 결과 검색",
        description = "문서 ID, 페이지 번호, 블록 ID를 기반으로 어휘 분석 결과를 검색합니다. " +
                     "페이지 번호와 블록 ID는 선택적 파라미터입니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "검색 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = VocabularyAnalysis.class)
            )
        )
    })
    @PostMapping("/search")
    public ResponseEntity<List<VocabularyAnalysis>> searchVocabularyAnalysis(
            @Parameter(description = "검색 조건", required = true)
            @RequestBody VocabularyAnalysisSearchRequest request) {
        log.info("어휘 분석 검색 요청: documentId={}, pageNumber={}, blockId={}", 
            request.getDocumentId(), request.getPageNumber(), request.getBlockId());
            
        List<VocabularyAnalysis> results = vocabularyAnalysisService.searchVocabularyAnalysis(
            request.getDocumentId(),
            request.getPageNumber(),
            request.getBlockId()
        );
        
        return ResponseEntity.ok(results);
    }
} 