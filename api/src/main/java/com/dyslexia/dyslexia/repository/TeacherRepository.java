package com.dyslexia.dyslexia.repository;

import com.dyslexia.dyslexia.entity.Teacher;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {
  Optional<Teacher> findByClientId(String clientId);

    boolean existsByMatchCodeAndIdNot(String matchCode, Long id);
    boolean existsByClientId(String clientId);

  Optional<Teacher> findByMatchCode(String code);
}