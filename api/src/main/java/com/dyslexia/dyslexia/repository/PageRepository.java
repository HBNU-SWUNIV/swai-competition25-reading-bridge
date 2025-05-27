package com.dyslexia.dyslexia.repository;

import com.dyslexia.dyslexia.entity.Document;
import com.dyslexia.dyslexia.entity.Page;
import com.dyslexia.dyslexia.enums.DocumentProcessStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PageRepository extends JpaRepository<Page, Long> {
    
    List<Page> findByDocument(Document document);
    
    List<Page> findByDocumentId(Long documentId);
    
    Optional<Page> findByDocumentAndPageNumber(Document document, Integer pageNumber);
    
    List<Page> findByProcessingStatus(DocumentProcessStatus status);
    
    List<Page> findByDocumentIdOrderByPageNumberAsc(Long documentId);
} 