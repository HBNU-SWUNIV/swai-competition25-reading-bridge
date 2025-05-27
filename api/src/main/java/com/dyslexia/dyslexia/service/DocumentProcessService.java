package com.dyslexia.dyslexia.service;

import com.dyslexia.dyslexia.domain.pdf.TextBlock;
import com.dyslexia.dyslexia.domain.pdf.VocabularyAnalysis;
import com.dyslexia.dyslexia.domain.pdf.VocabularyAnalysisRepository;
import com.dyslexia.dyslexia.entity.Document;
import com.dyslexia.dyslexia.entity.Page;
import com.dyslexia.dyslexia.entity.PageTip;
import com.dyslexia.dyslexia.entity.Teacher;
import com.dyslexia.dyslexia.enums.DocumentProcessStatus;
import com.dyslexia.dyslexia.enums.Grade;
import com.dyslexia.dyslexia.repository.DocumentRepository;
import com.dyslexia.dyslexia.repository.PageImageRepository;
import com.dyslexia.dyslexia.repository.PageRepository;
import com.dyslexia.dyslexia.repository.PageTipRepository;
import com.dyslexia.dyslexia.repository.TeacherRepository;
import com.dyslexia.dyslexia.util.DocumentProcessHolder;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
@RequiredArgsConstructor
public class DocumentProcessService {

    private final DocumentRepository documentRepository;
    private final PageRepository pageRepository;
    private final PageTipRepository pageTipRepository;
    private final PageImageRepository pageImageRepository;
    private final TeacherRepository teacherRepository;
    private final PdfParserService pdfParserService;
    private final AIPromptService aiPromptService;
    private final StorageService storageService;
    private final Executor taskExecutor;
    private final ObjectMapper objectMapper;
    private final DeepLTranslatorService deepLTranslatorService;
    private final VocabularyAnalysisRepository vocabularyAnalysisRepository;
    private final VocabularyAnalysisPromptService vocabularyAnalysisPromptService;

    // 스레드 풀 (문서 처리 전용)
    private static final int MAX_CONCURRENT_PAGES = 4;
    // 스레드 풀 (어휘 분석 전용)   
    private final ExecutorService vocabularyAnalysisExecutor = 
        Executors.newFixedThreadPool(Math.min(Runtime.getRuntime().availableProcessors() * 2, 16));  // 최대 16개로 제한


    @Transactional
    public Document uploadDocument(Long teacherId, MultipartFile file, String title, Grade grade, String subjectPath) throws IOException {
        Teacher teacher = teacherRepository.findById(teacherId)
            .orElseThrow(() -> new IllegalArgumentException("선생님을 찾을 수 없습니다."));

        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        
        log.info("파일 업로드 요청 처리 - 원본 파일명: {}, 교사ID: {}", 
                originalFilename, teacherId);

        Document document = Document.builder()
            .teacher(teacher)
            .title(title)
            .originalFilename(originalFilename)
            .filePath("temp_" + System.currentTimeMillis()) // 임시 경로 설정
            .fileSize(file.getSize())
            .mimeType(file.getContentType())
            .grade(grade)
            .subjectPath(subjectPath)
            .processStatus(DocumentProcessStatus.PENDING)
            .build();

        document = documentRepository.save(document);
        
        String uniqueFilename = UUID.randomUUID().toString() + fileExtension;
        
        String filePath = storageService.store(file, uniqueFilename, teacherId, document.getId());
        log.info("파일 저장 경로: {}", filePath);

        document.setFilePath(filePath);
        document = documentRepository.save(document);

        String folderPath = filePath;
        if (filePath != null && filePath.contains("/")) {
            folderPath = filePath.substring(0, filePath.lastIndexOf("/"));
        }
        log.info("PDF 폴더 경로: {}", folderPath);

        final Long documentId = document.getId();
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                CompletableFuture.runAsync(() -> processDocumentAsync(documentId), taskExecutor);
            }
        });

        return document;
    }

    private void processDocumentAsync(Long documentId) {
        try {
            log.info("문서 ID: {}의 비동기 처리를 시작합니다.", documentId);

            Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new IllegalArgumentException("문서를 찾을 수 없습니다."));

            document.setProcessStatus(DocumentProcessStatus.PROCESSING);
            documentRepository.save(document);

            List<String> rawPages = pdfParserService.parsePages(document.getFilePath());
            document.setPageCount(rawPages.size());
            documentRepository.save(document);

            log.info("문서 ID: {}의 전체 페이지 수: {}", documentId, rawPages.size());

            // 병렬 처리를 위한 CompletableFuture 리스트
            List<CompletableFuture<Void>> pageFutures = new ArrayList<>();
            
            /*
             * 스레드 풀 생성: 페이지 처리 병렬화를 위한 제한된 크기의 스레드 풀
             * MAX_CONCURRENT_PAGES 병렬 처리 스레드 수
             * Runtime.getRuntime().availableProcessors() 현재 시스템의 코어 수
             * 둘 중 작은 값을 선택하여 병렬 처리 스레드 수를 결정하는 이유는 시스템 자원을 효율적으로 사용하기 위함
             */
            ExecutorService pageProcessorExecutor = Executors.newFixedThreadPool(
                Math.min(MAX_CONCURRENT_PAGES, Runtime.getRuntime().availableProcessors())
            );
            
            try {
                // 각 페이지를 병렬로 처리
                for (int i = 0; i < rawPages.size(); i++) {
                    final int pageNumber = i + 1;
                    final String pageContent = rawPages.get(i);
                    
                    CompletableFuture<Void> pageFuture = CompletableFuture
                        .runAsync(() -> {
                            try {
                                log.info("문서 ID: {}, 페이지 번호: {} 병렬 처리 시작", documentId, pageNumber);
                                processPageWithTransaction(document, pageNumber, pageContent);
                                log.info("문서 ID: {}, 페이지 번호: {} 병렬 처리 완료", documentId, pageNumber);
                            } catch (Exception e) {
                                log.error("페이지 처리 중 오류 발생: 문서 ID: {}, 페이지 번호: {}", documentId, pageNumber, e);
                                throw e; // 상위 CompletableFuture에서 처리하도록 재발생
                            }
                        }, pageProcessorExecutor)
                        .exceptionally(ex -> {
                            log.error("페이지 처리 실패: 문서 ID: {}, 페이지 번호: {}", documentId, pageNumber, ex);
                            // 페이지 상태 업데이트 로직
                            updatePageStatusToFailed(document, pageNumber);
                            return null;
                        });
                    
                    pageFutures.add(pageFuture);
                }
                
                // 모든 페이지 처리 완료 대기
                CompletableFuture<Void> allPagesFuture = CompletableFuture.allOf(
                    pageFutures.toArray(new CompletableFuture[0])
                );
                
                try {
                    // 타임아웃 설정 (전체 문서 처리 제한 시간)
                    allPagesFuture.get(3, TimeUnit.HOURS);
                    
                    // 모든 페이지 처리 성공 시 문서 상태 업데이트
                    document.setProcessStatus(DocumentProcessStatus.COMPLETED);
                    documentRepository.save(document);
                    log.info("문서 ID: {}의 모든 처리가 병렬로 완료되었습니다.", documentId);
                } catch (TimeoutException e) {
                    log.error("문서 처리 타임아웃: 문서 ID: {}", documentId, e);
                    document.setProcessStatus(DocumentProcessStatus.FAILED);
                    documentRepository.save(document);
                } catch (Exception e) {
                    log.error("일부 페이지 처리 실패: 문서 ID: {}", documentId, e);
                    
                    // 완료된 페이지 수
                    long completedPages = pageRepository.findByDocumentId(documentId).stream()
                        .filter(p -> p.getProcessingStatus() == DocumentProcessStatus.COMPLETED)
                        .count();
                    
                    // 실패한 페이지 수
                    long failedPages = pageRepository.findByDocumentId(documentId).stream()
                        .filter(p -> p.getProcessingStatus() == DocumentProcessStatus.FAILED)
                        .count();
                    
                    // 미처리된 페이지 수
                    long pendingPages = rawPages.size() - completedPages - failedPages;
                    
                    log.error("문서 ID: {}의 처리 결과 - 총 페이지: {}, 완료: {}, 실패: {}, 미처리: {}", 
                              documentId, rawPages.size(), completedPages, failedPages, pendingPages);
                    
                    if (completedPages > 0 && completedPages == rawPages.size()) {
                        document.setProcessStatus(DocumentProcessStatus.COMPLETED);
                        log.info("문서 ID: {}의 모든 페이지가 처리 완료되었습니다.", documentId);
                    } else {
                        document.setProcessStatus(DocumentProcessStatus.FAILED);
                        log.error("문서 ID: {}의 처리가 실패했습니다. 일부 페이지({}/{})만 처리되었습니다.", 
                                 documentId, completedPages, rawPages.size());
                    }
                    documentRepository.save(document);
                }
            } finally {
                // 스레드 풀 종료
                pageProcessorExecutor.shutdown();
                try {
                    if (!pageProcessorExecutor.awaitTermination(15, TimeUnit.MINUTES)) {
                        log.error("문서 처리 작업이 15분 내에 완료되지 않아 강제 종료합니다. 처리 중인 페이지가 있을 수 있습니다.");
                        List<Runnable> notExecutedTasks = pageProcessorExecutor.shutdownNow();
                        log.error("실행되지 않은 작업 수: {}", notExecutedTasks.size());
                    } else {
                        log.info("모든 페이지 처리 작업이 정상적으로 완료되었습니다.");
                    }
                } catch (InterruptedException e) {
                    log.error("페이지 처리 작업 대기 중 인터럽트 발생: {}", e.getMessage());
                    List<Runnable> notExecutedTasks = pageProcessorExecutor.shutdownNow();
                    log.error("실행되지 않은 작업 수: {}", notExecutedTasks.size());
                    Thread.currentThread().interrupt();
                }
            }
        } catch (Exception e) {
            log.error("문서 처리 중 오류 발생: 문서 ID: {}", documentId, e);
            Document document = documentRepository.findById(documentId).orElse(null);
            if (document != null) {
                document.setProcessStatus(DocumentProcessStatus.FAILED);
                documentRepository.save(document);
                log.error("문서 ID: {}의 상태를 FAILED로 변경했습니다.", documentId);
            }
        }
    }
    
    /*
     * 페이지 상태를 실패로 변경
     */
    @Transactional
    public void updatePageStatusToFailed(Document document, int pageNumber) {
        Optional<Page> page = pageRepository.findByDocumentAndPageNumber(document, pageNumber);
        if (page.isPresent()) {
            page.get().setProcessingStatus(DocumentProcessStatus.FAILED);
            pageRepository.save(page.get());
            log.warn("페이지 상태를 FAILED로 변경: 문서 ID: {}, 페이지 번호: {}", document.getId(), pageNumber);
        }
    }
    
    /*
     * 쓰레드 풀 사용 시 트랜잭션 문제 해결을 위한 메서드
     */
    @Transactional
    public void processPageWithTransaction(Document document, int pageNumber, String rawContent) {
        processPage(document, pageNumber, rawContent);
    }

    @Transactional
    public void processPage(Document document, int pageNumber, String rawContent) {
        try {
            log.info("페이지 처리 시작: 문서 ID: {}, 페이지 번호: {}", document.getId(), pageNumber);
            
            // 파일 경로에서 폴더 경로 추출 (파일명 제외)
            String filePath = document.getFilePath();
            String folderPath = filePath;
            if (filePath != null && filePath.contains("/")) {
                folderPath = filePath.substring(0, filePath.lastIndexOf("/"));
            }
            
            // ThreadLocal에 문서 정보 설정
            DocumentProcessHolder.setDocumentId(document.getId());
            DocumentProcessHolder.setPdfName(document.getOriginalFilename());
            DocumentProcessHolder.setTeacherId(document.getTeacher().getId().toString());
            DocumentProcessHolder.setPdfFolderPath(folderPath);
            DocumentProcessHolder.setPageNumber(pageNumber); // 페이지 번호 설정 추가
            
            try {
                Optional<Page> existingPage = pageRepository.findByDocumentAndPageNumber(document, pageNumber);
                if (existingPage.isPresent() &&
                    existingPage.get().getProcessingStatus() == DocumentProcessStatus.COMPLETED) {
                    log.info("페이지가 이미 처리되었습니다: 문서 ID: {}, 페이지 번호: {}", document.getId(), pageNumber);
                    return;
                }

                // 1. OpenAI 번역 수행
                String translatedContent = aiPromptService.translateTextWithOpenAI(rawContent, Grade.GRADE_3);

                // 2. 번역된 텍스트로 AI Block 처리
                AIPromptService.PageBlockAnalysisResult blockAnalysisResult = aiPromptService.processPageContent(translatedContent, document.getGrade());

                // 3-1. blocks를 JSON 문자열로 변환
                String processedContentStr;
                try {
                    processedContentStr = objectMapper.writeValueAsString(blockAnalysisResult.getBlocks());
                    // log.info("변환된 블록 JSON: {}", processedContentStr);
                } catch (Exception e) {
                    log.error("블록을 JSON으로 변환 중 오류 발생", e);
                    throw new RuntimeException("블록을 JSON으로 변환하는 중 오류가 발생했습니다.", e);
                }

                // 3-2. blocks를 JSON Tree형식으로 변환
                com.fasterxml.jackson.databind.JsonNode processedContent;
                try {
                    processedContent = objectMapper.readTree(processedContentStr);
                } catch (Exception e) {
                    log.error("JSON 변환 중 오류 발생", e);
                    throw new RuntimeException("처리된 콘텐츠를 JSON으로 변환하는 중 오류가 발생했습니다.", e);
                }

                log.info("블럭 개수: {}", blockAnalysisResult.getBlocks().size());

                // 3-3. 텍스트 블록만 추출
                List<TextBlock> textBlocks = blockAnalysisResult.getBlocks().stream()
                    .filter(block -> block instanceof TextBlock textBlock && 
                            block.getType() != null && 
                            block.getType().name().equals("TEXT"))
                    .map(block -> (TextBlock) block)
                    .toList();

                log.info("textBlock 개수: {}", textBlocks.size());

                // 3-4. 텍스트 블록을 배치로 변환
                // List<Batch>: TextBlock 10개씩 묶음
                List<List<TextBlock>> batches = createBatches(textBlocks, 10);
                
                // 3-5. 각 배치를 별도 스레드에서 비동기 처리
                List<CompletableFuture<List<VocabularyAnalysis>>> batchFutures = new ArrayList<>();
                
                for (List<TextBlock> batch : batches) {
                    CompletableFuture<List<VocabularyAnalysis>> future = CompletableFuture.supplyAsync(
                        () -> analyzeVocabularyBatchImproved(batch, document.getId(), pageNumber),
                        vocabularyAnalysisExecutor
                    );
                    batchFutures.add(future);
                }
                
                // 3-6. 모든 배치 처리 완료 대기
                CompletableFuture<Void> allBatchesProcessed = CompletableFuture.allOf(
                    batchFutures.toArray(new CompletableFuture[0])
                );
                
                // 3-7. 결과 수집 및 저장
                List<VocabularyAnalysis> allEntities = new ArrayList<>();
                try {
                    allEntities = allBatchesProcessed
                        .thenApply(v -> batchFutures.stream()
                            .map(CompletableFuture::join)
                            .flatMap(List::stream)
                            .collect(Collectors.toList())
                        ).get(60, TimeUnit.MINUTES); // 타임아웃 설정
                    
                    // 모든 결과 일괄 저장
                    if (!allEntities.isEmpty()) {
                        saveVocabularyAnalysisInBatch(allEntities);
                        log.info("어휘 분석 완료: 문서 ID: {}, 페이지 번호: {}, 총 {}개 단어 처리", 
                            document.getId(), pageNumber, allEntities.size());
                    }
                } catch (Exception e) {
                    log.error("배치 비동기 처리 중 오류 발생: 문서 ID: {}, 페이지 번호: {}", 
                        document.getId(), pageNumber, e);
                }

                // 4. 메타데이터 추출 (번역된 텍스트 기반)
                String sectionTitle = aiPromptService.extractSectionTitle(translatedContent);
                Integer readingLevel = aiPromptService.calculateReadingLevel(translatedContent);
                Integer wordCount = aiPromptService.countWords(translatedContent);
                Float complexityScore = aiPromptService.calculateComplexityScore(translatedContent);

                // 5. 페이지 엔티티 생성 및 저장
                Page page;
                if (existingPage.isPresent()) {
                    page = existingPage.get();
                    page.setProcessingStatus(DocumentProcessStatus.PROCESSING);
                    pageRepository.save(page);
                } else {
                    page = Page.builder()
                        .document(document)
                        .pageNumber(pageNumber)
                        .originalContent(rawContent)
                        .processedContent(processedContent)
                        .sectionTitle(sectionTitle)
                        .readingLevel(readingLevel)
                        .wordCount(wordCount)
                        .complexityScore(complexityScore)
                        .processingStatus(DocumentProcessStatus.PROCESSING)
                        .build();

                    page = pageRepository.save(page);
                }

                // 페이지 데이터 초기화
                if (existingPage.isPresent()) {
                    pageTipRepository.deleteAll(pageTipRepository.findByPageId(page.getId()));
                    pageImageRepository.deleteAll(pageImageRepository.findByPageId(page.getId()));
                }

                // 7. 페이지 팁 추출
                log.info("용어 추출 시작: 문서 ID: {}, 페이지 번호: {}", document.getId(), pageNumber);
                List<AIPromptService.TermInfo> terms = aiPromptService.extractTerms(translatedContent, document.getGrade());
                for (AIPromptService.TermInfo termInfo : terms) {
                    PageTip pageTip = PageTip.builder()
                        .page(page)
                        .term(termInfo.getTerm())
                        .simplifiedExplanation(termInfo.getExplanation())
                        .termPosition(termInfo.getPositionJson())
                        .termType(termInfo.getTermType())
                        .visualAidNeeded(termInfo.isVisualAidNeeded())
                        .readAloudText(termInfo.getReadAloudText())
                        .build();

                    pageTipRepository.save(pageTip);
                }
                log.info("용어 {} 개 처리 완료: 문서 ID: {}, 페이지 번호: {}", terms.size(), document.getId(), pageNumber);

                page.setProcessingStatus(DocumentProcessStatus.COMPLETED);
                pageRepository.save(page);

                log.info("페이지 처리 완료: 문서 ID: {}, 페이지 번호: {}", document.getId(), pageNumber);
            } finally {
                // ThreadLocal 정리
                DocumentProcessHolder.clear();
            }

        } catch (Exception e) {
            log.error("페이지 처리 중 오류 발생: 문서 ID: {}, 페이지 번호: {}", document.getId(), pageNumber, e);

            Optional<Page> page = pageRepository.findByDocumentAndPageNumber(document, pageNumber);
            if (page.isPresent()) {
                page.get().setProcessingStatus(DocumentProcessStatus.FAILED);
                pageRepository.save(page.get());
                log.error("페이지 ID: {}의 상태를 FAILED로 변경했습니다.", page.get().getId());
            }
            
            // 오류 발생 시에도 ThreadLocal 반드시 정리
            DocumentProcessHolder.clear();
        }
    }

    /*
     * 개선된 배치 처리 메서드
     * 여러 텍스트 블록을 배치로 처리하고 음운 분석을 한 번에 수행
     */
    private List<VocabularyAnalysis> analyzeVocabularyBatchImproved(
        List<TextBlock> batch, Long documentId, int pageNumber) {
        
        List<VocabularyAnalysis> entities = new ArrayList<>();
        String threadName = Thread.currentThread().getName();
        
        try {
            log.info("배치 처리 시작 [스레드: {}]: 문서 ID: {}, 페이지: {}, 블록 수: {}", 
                threadName, documentId, pageNumber, batch.size());
            
            // 1. 배치 단위로 어휘 분석 → 어려운 어휘 추출
            String batchAnalysisJson = vocabularyAnalysisPromptService // JSON 형식의 String 반환
                .analyzeVocabularyBatchBasic(batch, 3);
            
            /*
             * Map<String, List<Map<String, Object>>>
             *        ↑      ↑         ↑       ↑
             *        │      │         │       └── 단어 정보 맵 (단어, 정의, 예시 등)
             *        │      │         └── 단어 정보 맵 (단어, 정의, 예시 등)
             *        │      └── 블록별 단어 목록
             *        └── 블록ID
             */
            // JSON String을 Map으로 변환
            Map<String, List<Map<String, Object>>> blockAnalysisMap = 
                objectMapper.readValue(batchAnalysisJson, 
                    new TypeReference<Map<String, List<Map<String, Object>>>>() {});
            
            // 2. 모든 블록에서 추출된 단어 수집 (중복 제거)
            Set<String> allWords = new HashSet<>();
            for (List<Map<String, Object>> wordsList : blockAnalysisMap.values()) {
                for (Map<String, Object> wordInfo : wordsList) {
                    allWords.add((String) wordInfo.get("word"));
                }
            }
            
            log.info("단어 추출 완료 [스레드: {}]: 문서 ID: {}, 페이지: {}, 단어 수: {}", 
                threadName, documentId, pageNumber, allWords.size());
            
            // 4. 각 블록별로 VocabularyAnalysis 엔티티 생성
            for (TextBlock textBlock : batch) {
                String blockId = textBlock.getId();
                List<Map<String, Object>> wordsList = blockAnalysisMap.get(blockId);
                
                if (wordsList == null) continue;
                
                for (Map<String, Object> wordInfo : wordsList) {
                    String word = (String) wordInfo.get("word");
                    // 통합 프롬프트 결과에서 바로 phoneme 정보 추출
                    Object phonemeAnalysisObj = wordInfo.get("phoneme");
                    String phonemeAnalysisJson = phonemeAnalysisObj != null ? 
                            objectMapper.writeValueAsString(phonemeAnalysisObj) : null;
                    
                    VocabularyAnalysis entity = VocabularyAnalysis.builder()
                        .documentId(documentId)
                        .pageNumber(pageNumber)
                        .blockId(blockId)
                        .word(word)
                        .startIndex((Integer) wordInfo.getOrDefault("startIndex", null))
                        .endIndex((Integer) wordInfo.getOrDefault("endIndex", null))
                        .definition((String) wordInfo.getOrDefault("definition", null))
                        .simplifiedDefinition((String) wordInfo.getOrDefault("simplifiedDefinition", null))
                        .examples(wordInfo.get("examples") != null ? 
                                 objectMapper.writeValueAsString(wordInfo.get("examples")) : null)
                        .difficultyLevel((String) wordInfo.getOrDefault("difficultyLevel", null))
                        .reason((String) wordInfo.getOrDefault("reason", null))
                        .gradeLevel(wordInfo.get("gradeLevel") instanceof Number ? 
                                  ((Number) wordInfo.get("gradeLevel")).intValue() : null)
                        .phonemeAnalysisJson(phonemeAnalysisJson)
                        .createdAt(java.time.LocalDateTime.now())
                        .build();
                    entities.add(entity);
                }
            }
            
            log.info("배치 처리 완료 [스레드: {}]: 문서 ID: {}, 페이지: {}, 음운 분석 엔티티 수: {}", 
                threadName, documentId, pageNumber, entities.size());
        } catch (Exception e) {
            log.error("배치 분석 처리 중 오류 [스레드: {}]: 문서 ID: {}, 페이지: {}", 
                threadName, documentId, pageNumber, e);
        }
        
        return entities;
    }

    @Transactional(readOnly = true)
    public DocumentProcessStatus getDocumentProcessStatus(Long documentId) {
        Document document = documentRepository.findById(documentId)
            .orElseThrow(() -> new IllegalArgumentException("문서를 찾을 수 없습니다."));
        return document.getProcessStatus();
    }

    @Transactional(readOnly = true)
    public int calculateDocumentProcessProgress(Long documentId) {
        Document document = documentRepository.findById(documentId)
            .orElseThrow(() -> new IllegalArgumentException("문서를 찾을 수 없습니다."));

        if (document.getProcessStatus() == DocumentProcessStatus.COMPLETED) {
            return 100;
        } else if (document.getProcessStatus() == DocumentProcessStatus.PENDING) {
            return 0;
        } else if (document.getProcessStatus() == DocumentProcessStatus.FAILED) {
            // 실패한 경우, 처리된 페이지 비율 계산
            long completedPages = pageRepository.findByDocumentId(documentId).stream()
                .filter(p -> p.getProcessingStatus() == DocumentProcessStatus.COMPLETED)
                .count();

            int totalPages = document.getPageCount() != null ? document.getPageCount() : 0;
            if (totalPages > 0) {
                return (int) ((completedPages * 100) / totalPages);
            }
            return 0;
        } else {
            // PROCESSING 상태인 경우
            long completedPages = pageRepository.findByDocumentId(documentId).stream()
                .filter(p -> p.getProcessingStatus() == DocumentProcessStatus.COMPLETED)
                .count();

            int totalPages = document.getPageCount() != null ? document.getPageCount() : 0;
            if (totalPages > 0) {
                return (int) ((completedPages * 100) / totalPages);
            }
            return 0;
        }
    }

    @Transactional
    public Document retryDocumentProcessing(Long documentId) {
        Document document = documentRepository.findById(documentId)
            .orElseThrow(() -> new IllegalArgumentException("문서를 찾을 수 없습니다."));

        if (document.getProcessStatus() != DocumentProcessStatus.FAILED) {
            throw new IllegalStateException("실패한 문서만 재처리할 수 있습니다.");
        }

        document.setProcessStatus(DocumentProcessStatus.PENDING);
        document = documentRepository.save(document);

        CompletableFuture.runAsync(() -> processDocumentAsync(documentId), taskExecutor);

        return document;
    }

    // 배치 생성 메서드 (추가)
    private List<List<TextBlock>> createBatches(List<TextBlock> textBlocks, int batchSize) {
        List<List<TextBlock>> batches = new ArrayList<>();
        for (int i = 0; i < textBlocks.size(); i += batchSize) {
            batches.add(textBlocks.subList(i, Math.min(i + batchSize, textBlocks.size())));
        }
        return batches;
    }

    // 배치 저장 메서드 (추가)
    private void saveVocabularyAnalysisInBatch(List<VocabularyAnalysis> entities) {
        vocabularyAnalysisRepository.saveAll(entities);
    }

    // 클래스 종료 시 스레드 풀 정리
    @jakarta.annotation.PreDestroy
    public void cleanup() {
        if (vocabularyAnalysisExecutor != null) {
            vocabularyAnalysisExecutor.shutdown();
            try {
                if (!vocabularyAnalysisExecutor.awaitTermination(1, TimeUnit.MINUTES)) {
                    vocabularyAnalysisExecutor.shutdownNow();
                }
            } catch (InterruptedException e) {
                vocabularyAnalysisExecutor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

} 