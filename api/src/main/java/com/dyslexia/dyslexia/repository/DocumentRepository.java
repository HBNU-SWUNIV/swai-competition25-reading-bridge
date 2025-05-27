package com.dyslexia.dyslexia.repository;

import com.dyslexia.dyslexia.entity.Document;
import com.dyslexia.dyslexia.entity.Teacher;
import com.dyslexia.dyslexia.enums.DocumentProcessStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    
    List<Document> findByTeacher(Teacher teacher);
    
    List<Document> findByTeacherId(Long teacherId);
    
    List<Document> findByProcessStatus(DocumentProcessStatus status);
    
    Optional<Document> findByIdAndTeacherId(Long id, Long teacherId);
    
    List<Document> findByTeacherIdOrderByCreatedAtDesc(Long teacherId);
    
    List<Document> findAllByOrderByCreatedAtDesc();
} 