package com.dyslexia.dyslexia.repository;

import com.dyslexia.dyslexia.entity.Page;
import com.dyslexia.dyslexia.entity.PageTip;
import com.dyslexia.dyslexia.enums.TermType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PageTipRepository extends JpaRepository<PageTip, Long> {
    
    List<PageTip> findByPage(Page page);
    
    List<PageTip> findByPageId(Long pageId);
    
    List<PageTip> findByPageIdAndTermType(Long pageId, TermType termType);
    
    List<PageTip> findByPageIdAndVisualAidNeeded(Long pageId, boolean visualAidNeeded);
} 