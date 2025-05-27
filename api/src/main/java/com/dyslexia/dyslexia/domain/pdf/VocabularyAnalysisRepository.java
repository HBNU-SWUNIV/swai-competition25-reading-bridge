package com.dyslexia.dyslexia.domain.pdf;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VocabularyAnalysisRepository extends JpaRepository<VocabularyAnalysis, Long> {
    List<VocabularyAnalysis> findByDocumentId(Long documentId);
    List<VocabularyAnalysis> findByDocumentIdAndPageNumber(Long documentId, Integer pageNumber);
    List<VocabularyAnalysis> findByDocumentIdAndPageNumberAndBlockId(Long documentId, Integer pageNumber, String blockId);
} 