package com.dyslexia.dyslexia.service;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class OcrService {

  public String extractText(MultipartFile file) throws IOException, TesseractException {
    Path tempPdf = Files.createTempFile("upload", ".pdf");
    file.transferTo(tempPdf);

    PDDocument document = PDDocument.load(tempPdf.toFile());
    PDFRenderer renderer = new PDFRenderer(document);
    StringBuilder extractedText = new StringBuilder();

    Tesseract tesseract = new Tesseract();

    String os = System.getProperty("os.name").toLowerCase();

    if (os.contains("win")) {
      // Windows 환경
      tesseract.setDatapath("C:\\Program Files\\Tesseract-OCR\\tessdata");
    } else {
      // Ubuntu 환경
      tesseract.setDatapath("/usr/share/tesseract-ocr/5.5.0/tessdata");
    }

    tesseract.setLanguage("kor+eng");

    for (int i = 0; i < document.getNumberOfPages(); i++) {
      BufferedImage image = renderer.renderImageWithDPI(i, 300);

      String text = tesseract.doOCR(image);
      extractedText.append(text).append("\n");
    }

    document.close();

    Files.deleteIfExists(tempPdf);

    return extractedText.toString();
  }
}
