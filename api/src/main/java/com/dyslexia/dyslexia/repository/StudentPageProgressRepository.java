package com.dyslexia.dyslexia.repository;

import com.dyslexia.dyslexia.entity.Page;
import com.dyslexia.dyslexia.entity.Student;
import com.dyslexia.dyslexia.entity.StudentPageProgress;
import com.dyslexia.dyslexia.enums.CompletionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentPageProgressRepository extends JpaRepository<StudentPageProgress, Long> {
    
    List<StudentPageProgress> findByStudent(Student student);
    
    List<StudentPageProgress> findByStudentId(Long studentId);
    
    List<StudentPageProgress> findByPage(Page page);
    
    List<StudentPageProgress> findByPageId(Long pageId);
    
    Optional<StudentPageProgress> findByStudentIdAndPageId(Long studentId, Long pageId);
    
    List<StudentPageProgress> findByStudentIdAndCompletionStatus(Long studentId, CompletionStatus status);
    
    List<StudentPageProgress> findByStudentIdAndPageIdIn(Long studentId, List<Long> pageIds);
}