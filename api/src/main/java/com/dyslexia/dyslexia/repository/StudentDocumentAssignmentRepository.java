package com.dyslexia.dyslexia.repository;

import com.dyslexia.dyslexia.entity.Document;
import com.dyslexia.dyslexia.entity.Student;
import com.dyslexia.dyslexia.entity.StudentDocumentAssignment;
import com.dyslexia.dyslexia.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentDocumentAssignmentRepository extends JpaRepository<StudentDocumentAssignment, Long> {
    
    List<StudentDocumentAssignment> findByStudent(Student student);
    
    List<StudentDocumentAssignment> findByStudentId(Long studentId);
    
    List<StudentDocumentAssignment> findByDocument(Document document);
    
    List<StudentDocumentAssignment> findByDocumentId(Long documentId);
    
    Optional<StudentDocumentAssignment> findByStudentIdAndDocumentId(Long studentId, Long documentId);
    
    List<StudentDocumentAssignment> findByAssignedBy(Teacher teacher);
    
    List<StudentDocumentAssignment> findByAssignedById(Long teacherId);

    List<StudentDocumentAssignment> findByAssignedByIdAndStudentId(Long teacherId, Long studentId);
} 