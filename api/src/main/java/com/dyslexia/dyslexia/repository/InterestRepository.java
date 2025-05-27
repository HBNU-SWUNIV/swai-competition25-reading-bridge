package com.dyslexia.dyslexia.repository;

import com.dyslexia.dyslexia.entity.Interest;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InterestRepository extends JpaRepository<Interest, Long> {

  Optional<Interest> findByName(String name);
}
