package com.dyslexia.dyslexia.service;

import com.dyslexia.dyslexia.dto.AuthResponseDto;
import com.dyslexia.dyslexia.entity.Student;
import com.dyslexia.dyslexia.entity.Teacher;
import com.dyslexia.dyslexia.enums.UserType;
import com.dyslexia.dyslexia.repository.StudentRepository;
import com.dyslexia.dyslexia.repository.TeacherRepository;
import com.dyslexia.dyslexia.util.JwtTokenProvider;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class KakaoUserService {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final JwtTokenProvider jwtTokenProvider;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;

    @Value("${kakao.client.id}")
    private String clientId;

    @Value("${kakao.client.secret}")
    private String clientSecret;

    @Value("${kakao.client.redirect-uri}")
    private String redirectUri;

    public AuthResponseDto processKakaoCallback(String code) {
        String accessToken = getAccessToken(code);
        JsonNode userInfo = getUserInfo(accessToken);
        
        String kakaoId = userInfo.get("id").asText();
        String nickname = getNickname(userInfo);
        
        UserType userType = determineUserType(kakaoId);
        boolean isRegistered = userType != UserType.UNREGISTERED;
        
        if (!isRegistered) {
            return AuthResponseDto.builder()
                .registered(false)
                .clientId(kakaoId)
                .nickname(nickname)
                .userType(userType.name())
                .build();
        }
        
        String accessTokenJwt = jwtTokenProvider.createAccessToken(kakaoId, userType.name());
        String refreshTokenJwt = jwtTokenProvider.createRefreshToken(kakaoId);
        
        return AuthResponseDto.builder()
            .registered(true)
            .clientId(kakaoId)
            .nickname(nickname)
            .userType(userType.name())
            .accessToken(accessTokenJwt)
            .refreshToken(refreshTokenJwt)
            .build();
    }

    private String getNickname(JsonNode userInfo) {
        try {
            JsonNode properties = userInfo.get("properties");
            if (properties != null && properties.has("nickname")) {
                String nickname = properties.get("nickname").asText();
                return nickname != null && !nickname.isEmpty() ? nickname : "사용자";
            }
        } catch (Exception e) {
            // 닉네임을 가져오는데 실패한 경우 기본값 반환
        }
        return "사용자";
    }

    private String getAccessToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("redirect_uri", redirectUri);
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        ResponseEntity<String> response = restTemplate.exchange(
            "https://kauth.kakao.com/oauth/token",
            HttpMethod.POST,
            request,
            String.class
        );

        try {
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            return jsonNode.get("access_token").asText();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get access token", e);
        }
    }

    private JsonNode getUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(
            "https://kapi.kakao.com/v2/user/me",
            HttpMethod.GET,
            request,
            String.class
        );

        try {
            return objectMapper.readTree(response.getBody());
        } catch (Exception e) {
            throw new RuntimeException("Failed to get user info", e);
        }
    }

    private UserType determineUserType(String kakaoId) {
        if (studentRepository.findByClientId(kakaoId).isPresent()) {
            return UserType.STUDENT;
        }
        if (teacherRepository.findByClientId(kakaoId).isPresent()) {
            return UserType.TEACHER;
        }
        return UserType.UNREGISTERED;
    }
} 