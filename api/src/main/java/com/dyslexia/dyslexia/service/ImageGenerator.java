package com.dyslexia.dyslexia.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageGenerator {
    private static final String REPLICATE_API_URL = "https://api.replicate.com/v1/models/black-forest-labs/flux-1.1-pro/predictions";
    private static final Set<String> SUPPORTED_ASPECT_RATIOS = Set.of("2:3", "16:9");

    @Value("${replicate.api.key}")
    private String replicateApiKey;

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    private final RestTemplate restTemplate;

    public String generateImage(String prompt, String aspectRatio) {
        if (prompt == null || prompt.trim().isEmpty()) {
            throw new IllegalArgumentException("prompt는 null이거나 비어 있을 수 없습니다.");
        }
        if (!SUPPORTED_ASPECT_RATIOS.contains(aspectRatio)) {
            throw new IllegalArgumentException("지원하지 않는 비율: " + aspectRatio);
        }
        HttpEntity<Map<String, Object>> request = buildRequest(prompt, aspectRatio);
        ResponseEntity<Map> response = restTemplate.exchange(
                REPLICATE_API_URL,
                HttpMethod.POST,
                request,
                Map.class
        );
        log.info(response.getBody().toString());

        Map body = response.getBody();
        if (body == null) throw new RuntimeException("응답이 비어 있습니다.");
        Object output = body.get("output");
        String imageUrl = null;
        if (output instanceof List outputList && !outputList.isEmpty()) {
            Object url = outputList.get(0);
            if (url != null && !url.toString().isBlank()) {
                imageUrl = url.toString();
            }
        } else if (output instanceof String str && !str.isBlank()) {
            imageUrl = str;
        }
        if (imageUrl == null) throw new RuntimeException("이미지 URL을 찾을 수 없습니다.");
        String imageId = saveImageFromUrl(imageUrl);
        return imageId;
    }

    private HttpHeaders createAuthHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + replicateApiKey);
        return headers;
    }

    private HttpEntity<Map<String, Object>> buildRequest(String prompt, String aspectRatio) {
        HttpHeaders headers = createAuthHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Prefer", "wait=60");
        Map<String, Object> body = new HashMap<>();
//        body.put("version", "0s78r9y3tdrge0cjf05sxnjg8w");
        Map<String, Object> input = new HashMap<>();
        input.put("prompt", prompt);
        input.put("aspect_ratio", aspectRatio);
        body.put("input", input);
        return new HttpEntity<>(body, headers);
    }

    private String saveImageFromUrl(String imageUrl) {
        try {
            String imageId = UUID.randomUUID().toString();
            String ext = getFileExtension(imageUrl);
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();
            File file = new File(dir, imageId + ext);

            java.net.URL url = new java.net.URL(imageUrl);
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Authorization", "Bearer " + replicateApiKey);
            conn.connect();

            try (InputStream in = conn.getInputStream()) {
                java.nio.file.Files.copy(in, file.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            } finally {
                conn.disconnect();
            }
            String absPath = file.getAbsolutePath();
            String basePath = new File(uploadDir).getAbsolutePath();
            String relativePath = absPath.startsWith(basePath) ? absPath.substring(basePath.length()) : absPath;
            if (relativePath.startsWith(File.separator)) relativePath = relativePath.substring(1);
            return relativePath.replace(File.separatorChar, '/');   
        } catch (Exception e) {
            throw new RuntimeException("이미지 저장 실패", e);
        }
    }

    private String getFileExtension(String url) {
        int idx = url.lastIndexOf('.');
        if (idx > 0 && idx < url.length() - 1) {
            String ext = url.substring(idx);
            if (ext.length() <= 5) return ext;
        }
        return ".jpg";
    }
}
