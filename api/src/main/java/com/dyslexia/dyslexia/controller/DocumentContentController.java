package com.dyslexia.dyslexia.controller;

import com.dyslexia.dyslexia.dto.PageContentResponse;
import com.dyslexia.dyslexia.dto.PageTipResponse;
import com.dyslexia.dyslexia.entity.Page;
import com.dyslexia.dyslexia.entity.PageTip;
import com.dyslexia.dyslexia.service.DocumentContentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("document-contents")
@RequiredArgsConstructor
@Tag(name = "Document Content API", description = "PDF 문서 콘텐츠 관련 API")
public class DocumentContentController {

    private final DocumentContentService documentContentService;
    private final ObjectMapper objectMapper;

    @GetMapping("/pages")
    @Operation(
        summary = "문서 페이지 조회", 
        description = "문서 ID 기반으로 생성된 콘텐츠 JSON(page)를 조회합니다. 페이지 번호를 지정하면 특정 페이지만 조회합니다.\n\nBlock 구조 예시:\n[\n  {\"id\":\"1\",\"type\":\"HEADING1\",\"text\":\"챕터 제목\"},\n  {\"id\":\"2\",\"type\":\"TEXT\",\"text\":\"본문\"},\n  {\"id\":\"3\",\"type\":\"LIST\",\"items\":[\"항목1\",\"항목2\"]}\n]",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "페이지 목록 조회 성공",
                content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(
                        schema = @Schema(implementation = PageContentResponse.class)
                    )
                )
            )
        }
    )
    public ResponseEntity<List<PageContentResponse>> getDocumentPages(
            @Parameter(description = "문서 ID", required = true) @RequestParam(name = "documentId") Long documentId,
            @Parameter(description = "페이지 번호 (선택사항)") @RequestParam(name = "page", required = false) Integer pageNumber) {
        List<Page> pages = documentContentService.getPagesByDocumentId(documentId, pageNumber);
        List<PageContentResponse> responses = pages.stream()
                .map(page -> PageContentResponse.fromEntity(page, objectMapper))
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/pages/{pageId}/tips")
    @Operation(
        summary = "페이지 팁 조회", 
        description = "페이지 ID 기반으로 해당 페이지의 팁을 조회합니다.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "페이지 팁 목록 조회 성공",
                content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(
                        schema = @Schema(implementation = PageTipResponse.class)
                    )
                )
            )
        }
    )
    public ResponseEntity<List<PageTipResponse>> getPageTips(
            @Parameter(description = "페이지 ID", required = true) @PathVariable("pageId") Long pageId) {
        List<PageTip> pageTips = documentContentService.getPageTipsByPageId(pageId);
        List<PageTipResponse> responses = pageTips.stream()
                .map(PageTipResponse::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

//    @GetMapping("/pages/{pageId}/images")
//    @Operation(
//        summary = "페이지 이미지 조회",
//        description = "페이지 ID 기반으로 해당 페이지의 이미지를 조회합니다.",
//        responses = {
//            @ApiResponse(
//                responseCode = "200",
//                description = "페이지 이미지 목록 조회 성공",
//                content = @Content(
//                    mediaType = "application/json",
//                    array = @ArraySchema(
//                        schema = @Schema(implementation = PageImageResponse.class)
//                    )
//                )
//            )
//        }
//    )
//    public ResponseEntity<List<PageImageResponse>> getPageImages(
//            @Parameter(description = "페이지 ID", required = true) @PathVariable("pageId") Long pageId) {
//        List<PageImage> pageImages = documentContentService.getPageImagesByPageId(pageId);
//        List<PageImageResponse> responses = pageImages.stream()
//                .map(PageImageResponse::fromEntity)
//                .collect(Collectors.toList());
//        return ResponseEntity.ok(responses);
//    }

//    @GetMapping(value = "/pages/images/{teacherId}/{documentId}/{pageNumber}/{blockId}")
//    @Operation(
//        summary = "페이지 이미지 조회",
//        description = "특정 페이지와 블록에 연결된 이미지를 조회합니다."
//    )
//    public ResponseEntity<byte[]> getImage(
//        @Parameter(description = "교사 ID", required = true) @PathVariable("teacherId") Long teacherId,
//        @Parameter(description = "문서 ID", required = true) @PathVariable("documentId") Long documentId,
//        @Parameter(description = "페이지 번호", required = true) @PathVariable("pageNumber") Integer pageNumber,
//        @Parameter(description = "블록 ID", required = true) @PathVariable("blockId") String blockId) {
//
//        byte[] imageData = documentContentService.getImage(teacherId, documentId, pageNumber, blockId);
//
//        return ResponseEntity.ok()
//            .contentType(MediaType.parseMediaType("image/svg+xml"))
//            .contentLength(imageData.length)
//            .header("Cache-Control", "max-age=86400")
//            .body(imageData);
//    }
}