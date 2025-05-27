package com.dyslexia.dyslexia.service;

import com.dyslexia.dyslexia.dto.AccessibilitySettingsUpdateRequest;
import com.dyslexia.dyslexia.dto.PageDetailResponseDto;
import com.dyslexia.dyslexia.dto.PageProgressUpdateRequest;
import com.dyslexia.dyslexia.dto.ResponseDto;
import com.dyslexia.dyslexia.entity.*;
import com.dyslexia.dyslexia.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class StudentDocumentService {

    private final StudentRepository studentRepository;
    private final DocumentRepository documentRepository;
    private final PageRepository pageRepository;
    private final PageTipRepository pageTipRepository;
    private final PageImageRepository pageImageRepository;
    private final StudentPageProgressRepository studentPageProgressRepository;
    private final PageAccessibilitySettingsRepository pageAccessibilitySettingsRepository;
    private final StudentDocumentAssignmentRepository studentDocumentAssignmentRepository;

    @Transactional(readOnly = true)
    public ResponseEntity<PageDetailResponseDto> getPageDetail(Long studentId, Long pageId) {
        log.info("페이지 상세 정보 조회: 학생 ID: {}, 페이지 ID: {}", studentId, pageId);
        
        Page page = pageRepository.findById(pageId)
            .orElseThrow(() -> new IllegalArgumentException("페이지를 찾을 수 없습니다."));
        
        Document document = page.getDocument();
        boolean hasAccess = studentDocumentAssignmentRepository
            .findByStudentIdAndDocumentId(studentId, document.getId())
            .isPresent();
        
        if (!hasAccess) {
            return ResponseEntity.status(403).body(
                PageDetailResponseDto.builder()
                    .success(false)
                    .message("이 페이지에 대한 접근 권한이 없습니다.")
                    .build()
            );
        }
        
        List<PageTip> tips = pageTipRepository.findByPageId(pageId);
        List<PageImage> images = pageImageRepository.findByPageId(pageId);
        Optional<StudentPageProgress> progress = studentPageProgressRepository
            .findByStudentIdAndPageId(studentId, pageId);
        Optional<PageAccessibilitySettings> settings = pageAccessibilitySettingsRepository
            .findByStudentIdAndPageId(studentId, pageId);
        
        // 응답 구성
        return ResponseEntity.ok(
            PageDetailResponseDto.builder()
                .success(true)
                .message("페이지 상세 정보 조회 성공")
                .page(page)
                .tips(tips)
                .images(images)
                .progress(progress.orElse(null))
                .settings(settings.orElse(null))
                .build()
        );
    }
    
    @Transactional
    public ResponseEntity<ResponseDto> updatePageProgress(Long studentId, Long pageId, PageProgressUpdateRequest request) {
        log.info("페이지 진행 상태 업데이트: 학생 ID: {}, 페이지 ID: {}", studentId, pageId);
        
        Page page = pageRepository.findById(pageId)
            .orElseThrow(() -> new IllegalArgumentException("페이지를 찾을 수 없습니다."));
        
        Student student = studentRepository.findById(studentId)
            .orElseThrow(() -> new IllegalArgumentException("학생을 찾을 수 없습니다."));
        
        Document document = page.getDocument();
        boolean hasAccess = studentDocumentAssignmentRepository
            .findByStudentIdAndDocumentId(studentId, document.getId())
            .isPresent();
        
        if (!hasAccess) {
            return ResponseEntity.status(403).body(
                ResponseDto.builder()
                    .success(false)
                    .message("이 페이지에 대한 접근 권한이 없습니다.")
                    .build()
            );
        }
        
        StudentPageProgress progress = studentPageProgressRepository
            .findByStudentIdAndPageId(studentId, pageId)
            .orElse(
                StudentPageProgress.builder()
                    .student(student)
                    .page(page)
                    .build()
            );
        
        progress.setTimeSpentSeconds(request.getTimeSpentSeconds());
        progress.setCompletionStatus(request.getCompletionStatus());
        progress.setRetryCount(request.getRetryCount());
        progress.setComprehensionScore(request.getComprehensionScore());
        progress.setNotes(request.getNotes());
        progress.setLastAccessedAt(LocalDateTime.now());
        
        if (request.getUsedTipIds() != null && request.getUsedTipIds().length > 0) {
            String usedTipIdsStr = String.join(",", 
                java.util.Arrays.stream(request.getUsedTipIds())
                    .map(String::valueOf)
                    .collect(Collectors.toList())
            );
            progress.setUsedTipIds(usedTipIdsStr);
        }
        
        studentPageProgressRepository.save(progress);
        
        return ResponseEntity.ok(
            ResponseDto.builder()
                .success(true)
                .message("페이지 진행 상태가 업데이트되었습니다.")
                .build()
        );
    }
    
    @Transactional
    public ResponseEntity<ResponseDto> updateAccessibilitySettings(Long studentId, Long pageId, AccessibilitySettingsUpdateRequest request) {
        log.info("페이지 접근성 설정 업데이트: 학생 ID: {}, 페이지 ID: {}", studentId, pageId);
        
        Page page = pageRepository.findById(pageId)
            .orElseThrow(() -> new IllegalArgumentException("페이지를 찾을 수 없습니다."));
        
        Student student = studentRepository.findById(studentId)
            .orElseThrow(() -> new IllegalArgumentException("학생을 찾을 수 없습니다."));
        
        Document document = page.getDocument();
        boolean hasAccess = studentDocumentAssignmentRepository
            .findByStudentIdAndDocumentId(studentId, document.getId())
            .isPresent();
        
        if (!hasAccess) {
            return ResponseEntity.status(403).body(
                ResponseDto.builder()
                    .success(false)
                    .message("이 페이지에 대한 접근 권한이 없습니다.")
                    .build()
            );
        }
        
        if (Boolean.TRUE.equals(request.getApplyToEntireDocument())) {
            List<Page> documentPages = pageRepository.findByDocumentId(document.getId());
            
            for (Page docPage : documentPages) {
                updateSinglePageSettings(student, docPage, request);
            }
            
            return ResponseEntity.ok(
                ResponseDto.builder()
                    .success(true)
                    .message("문서 전체의 접근성 설정이 업데이트되었습니다.")
                    .build()
            );
        } else {
            updateSinglePageSettings(student, page, request);
            
            return ResponseEntity.ok(
                ResponseDto.builder()
                    .success(true)
                    .message("페이지 접근성 설정이 업데이트되었습니다.")
                    .build()
            );
        }
    }
    
    private void updateSinglePageSettings(Student student, Page page, AccessibilitySettingsUpdateRequest request) {
        PageAccessibilitySettings settings = pageAccessibilitySettingsRepository
            .findByStudentIdAndPageId(student.getId(), page.getId())
            .orElse(
                PageAccessibilitySettings.builder()
                    .student(student)
                    .page(page)
                    .document(page.getDocument())
                    .build()
            );
        
        settings.setFontSize(request.getFontSize());
        settings.setLineSpacing(request.getLineSpacing());
        settings.setLetterSpacing(request.getLetterSpacing());
        settings.setColorScheme(request.getColorScheme());
        settings.setTextToSpeechEnabled(request.getTextToSpeechEnabled());
        settings.setReadingHighlightEnabled(request.getReadingHighlightEnabled());
        settings.setCustomBackgroundColor(request.getCustomBackgroundColor());
        settings.setCustomTextColor(request.getCustomTextColor());
        settings.setLastModifiedAt(LocalDateTime.now());
        
        pageAccessibilitySettingsRepository.save(settings);
        
        if (Boolean.TRUE.equals(request.getSaveAsDefault())) {
            student.setDefaultFontSize(request.getFontSize());
            student.setDefaultLineSpacing(request.getLineSpacing());
            student.setDefaultLetterSpacing(request.getLetterSpacing());
            student.setDefaultColorScheme(request.getColorScheme());
            student.setDefaultTextToSpeechEnabled(request.getTextToSpeechEnabled());
            student.setDefaultReadingHighlightEnabled(request.getReadingHighlightEnabled());
            student.setDefaultBackgroundColor(request.getCustomBackgroundColor());
            student.setDefaultTextColor(request.getCustomTextColor());
            
            studentRepository.save(student);
        }
    }
} 