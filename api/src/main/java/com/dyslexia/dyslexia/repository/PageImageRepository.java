package com.dyslexia.dyslexia.repository;

import com.dyslexia.dyslexia.entity.Page;
import com.dyslexia.dyslexia.entity.PageImage;
import com.dyslexia.dyslexia.enums.ImageType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PageImageRepository extends JpaRepository<PageImage, Long> {
    
    List<PageImage> findByPage(Page page);
    
    List<PageImage> findByPageId(Long pageId);
    
    List<PageImage> findByPageIdAndImageType(Long pageId, ImageType imageType);
    
    List<PageImage> findByConceptReference(String conceptReference);
} 