package com.dyslexia.dyslexia.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PdfParserService {

    public List<String> parsePages(String filePath) throws IOException {
        log.info("PDF 파싱 시작: {}", filePath);
        
        List<String> pages = new ArrayList<>();
        
        try (PDDocument document = PDDocument.load(new File(filePath))) {
            PDFTextStripper stripper = new PDFTextStripper();
            int pageCount = document.getNumberOfPages();
            
            for (int i = 1; i <= pageCount; i++) {
                stripper.setStartPage(i);
                stripper.setEndPage(i);
                String pageText = stripper.getText(document);
                pages.add(pageText);
                log.debug("페이지 {} 파싱 완료, 글자 수: {}", i, pageText.length());
            }
            
            log.info("PDF 파싱 완료: 전체 {} 페이지", pageCount);
        }
        
        return pages;
    }
    
    public List<List<String>> extractImages(String filePath) throws IOException {
        log.info("PDF 이미지 추출 시작: {}", filePath);
        
        /**
         * 이 메서드는 PDF라이브러리를 사용하여 
         *PDF에서 이미지를 추출하고 저장하는 로직을 구현해야함
        현재는 구현 생략 - 은기
         */
        
        return new ArrayList<>();
    }
} 