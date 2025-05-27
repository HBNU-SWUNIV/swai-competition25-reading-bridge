package com.dyslexia.dyslexia.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
@Slf4j
@RequiredArgsConstructor
public class StorageService {

    @Value("${app.upload.dir:./uploads}")
    private String uploadDir;

    @PostConstruct
    public void init() {
        try {
            Path path = Paths.get(uploadDir);
            log.info("업로드 디렉토리 초기화 시도: {}", path.toAbsolutePath());
            
            if (!Files.exists(path)) {
                Files.createDirectories(path);
                log.info("업로드 디렉토리 생성 완료: {}", path.toAbsolutePath());
            } else {
                log.info("업로드 디렉토리가 이미 존재합니다: {}", path.toAbsolutePath());
            }
            log.info("업로드 디렉토리 초기화가 완료되었습니다: {}", path.toAbsolutePath());
        } catch (IOException e) {
            log.error("업로드 디렉토리 초기화 실패", e);
            throw new RuntimeException("업로드 디렉토리를 생성할 수 없습니다.", e);
        }
    }


    public String store(MultipartFile file, String uniqueFilename, Long teacherId, Long documentId) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어 있습니다.");
        }

        try {
            log.info("Document ID 기반 파일 저장 요청 - 파일명: {}, 교사ID: {}, Document ID: {}, 업로드 디렉토리: {}", 
                    uniqueFilename, teacherId, documentId, uploadDir);
            
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                log.error("업로드 디렉토리가 존재하지 않습니다: {}", uploadPath.toAbsolutePath());
                Files.createDirectories(uploadPath);
                log.info("업로드 디렉토리 재생성 완료: {}", uploadPath.toAbsolutePath());
            }
            
            String teacherFolderPath = teacherId.toString();
            Path teacherDir = uploadPath.resolve(teacherFolderPath);

            if (!Files.exists(teacherDir)) {
                Files.createDirectories(teacherDir);
                log.info("교사 디렉토리 생성 완료: {}", teacherDir.toAbsolutePath());
            }
            
            Path documentDir = teacherDir.resolve(documentId.toString());

            if (!Files.exists(documentDir)) {
                Files.createDirectories(documentDir);
                log.info("Document 디렉토리 생성 완료: {}", documentDir.toAbsolutePath());
            }
            
            Path destinationFile = documentDir.resolve(uniqueFilename)
                    .normalize().toAbsolutePath();

            if (!destinationFile.startsWith(documentDir.toAbsolutePath())) {
                log.error("보안 오류: {} 파일을 {} 디렉토리 외부에 저장하려고 합니다", uniqueFilename, documentDir);
                throw new IllegalStateException("보안 문제: 지정된 디렉토리 외부에 파일을 저장할 수 없습니다.");
            }
            
            Files.copy(file.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);
            
            log.info("파일 저장 완료: {}", destinationFile);
            
            return destinationFile.toString();
            
        } catch (IOException e) {
            log.error("파일 저장 중 오류 발생: {}", e.getMessage(), e);
            throw new IOException("파일을 저장할 수 없습니다: " + e.getMessage(), e);
        }
    }
    
    public Path load(String filePath) {
        Path path = Paths.get(filePath);
        log.debug("파일 로드 요청: {}", path.toAbsolutePath());
        return path;
    }

    public boolean exists(String filePath) {
        Path path = load(filePath);
        boolean exists = Files.exists(path);
        log.debug("파일 존재 확인: {}, 결과: {}", path.toAbsolutePath(), exists);
        return exists;
    }

    public boolean delete(String filePath) {
        try {
            Path file = load(filePath);
            log.info("파일 삭제 요청: {}", file.toAbsolutePath());
            boolean deleted = Files.deleteIfExists(file);
            log.info("파일 삭제 결과: {}", deleted ? "성공" : "실패");
            return deleted;
        } catch (IOException e) {
            log.error("파일 삭제 중 오류 발생: {}", filePath, e);
            return false;
        }
    }
}