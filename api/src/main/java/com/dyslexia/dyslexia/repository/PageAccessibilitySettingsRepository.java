package com.dyslexia.dyslexia.repository;

import com.dyslexia.dyslexia.entity.PageAccessibilitySettings;
import com.dyslexia.dyslexia.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PageAccessibilitySettingsRepository extends JpaRepository<PageAccessibilitySettings, Long> {
    
    List<PageAccessibilitySettings> findByStudent(Student student);
    
    List<PageAccessibilitySettings> findByStudentId(Long studentId);
    
    Optional<PageAccessibilitySettings> findByStudentIdAndPageId(Long studentId, Long pageId);
    
    List<PageAccessibilitySettings> findByStudentIdAndDocumentId(Long studentId, Long documentId);
} 