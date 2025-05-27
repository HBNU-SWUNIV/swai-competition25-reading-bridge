package com.dyslexia.dyslexia.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dyslexia.dyslexia.entity.AIResponse;

@Repository
public interface AIResponseRepository extends JpaRepository<AIResponse, Long> {

    
}
