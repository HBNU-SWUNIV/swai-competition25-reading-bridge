package com.dyslexia.dyslexia.service;

import com.dyslexia.dyslexia.entity.Page;
import com.dyslexia.dyslexia.entity.PageImage;
import com.dyslexia.dyslexia.entity.PageTip;
import com.dyslexia.dyslexia.repository.DocumentRepository;
import com.dyslexia.dyslexia.repository.PageImageRepository;
import com.dyslexia.dyslexia.repository.PageRepository;
import com.dyslexia.dyslexia.repository.PageTipRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DocumentContentService {

    private final DocumentRepository documentRepository;
    private final PageRepository pageRepository;
    private final PageTipRepository pageTipRepository;
    private final PageImageRepository pageImageRepository;
    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    public List<Page> getPagesByDocumentId(Long documentId, Integer pageNumber) {
        if (pageNumber != null) {
            return documentRepository.findById(documentId)
                    .map(document -> pageRepository.findByDocumentAndPageNumber(document, pageNumber)
                            .map(List::of)
                            .orElse(List.of()))
                    .orElse(List.of());
        }
        return pageRepository.findByDocumentIdOrderByPageNumberAsc(documentId);
    }

    public List<PageTip> getPageTipsByPageId(Long pageId) {
        return pageTipRepository.findByPageId(pageId);
    }

    public List<PageImage> getPageImagesByPageId(Long pageId) {
        return pageImageRepository.findByPageId(pageId);
    }

//    public byte[] getImage(Long teacherId, Long documentId, Integer pageNumber, String blockId) {
//        String imagePath = String.format("%s/%s/%s/%s/%s", uploadDir, teacherId, documentId, pageNumber, blockId + ".svg");
//
//        try {
//            Path filePath = Paths.get(imagePath);
//            if (Files.exists(filePath)) {
//                return Files.readAllBytes(filePath);
//            } else {
//                throw new RuntimeException("Image not found");
//            }
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
}