package com.dyslexia.dyslexia.service;

import com.dyslexia.dyslexia.dto.AuthResponseDto;
import com.dyslexia.dyslexia.enums.UserType;
import com.dyslexia.dyslexia.repository.StudentRepository;
import com.dyslexia.dyslexia.repository.TeacherRepository;
import com.dyslexia.dyslexia.util.JwtTokenProvider;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoService {

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

        String clientId = userInfo.get("id").asText();
        String nickname = userInfo.get("kakao_account")
            .get("profile")
            .get("nickname")
            .asText();

        UserType userType = determineUserType(clientId);



        if (userType != UserType.UNREGISTERED) {
            String newAccessToken = jwtTokenProvider.createAccessToken(clientId, userType.name());
            String newRefreshToken = jwtTokenProvider.createRefreshToken(clientId);

            return AuthResponseDto.builder()
                .registered(true)
                .clientId(clientId)
                .nickname(nickname)
                .userType(userType.name())
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
        }

        return AuthResponseDto.builder()
            .registered(false)
            .clientId(clientId)
            .nickname(nickname)
            .userType(UserType.UNREGISTERED.name())
            .build();
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
        log.info(response.getBody());

        try {
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            return jsonNode.get("access_token").asText();
        } catch (Exception e) {
            log.error("Failed to get access token", e);
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
        log.info(response.getBody());

        try {
            return objectMapper.readTree(response.getBody());
        } catch (Exception e) {
            log.error("Failed to get user info", e);
            throw new RuntimeException("Failed to get user info", e);
        }
    }

    private UserType determineUserType(String clientId) {
        if (studentRepository.findByClientId(clientId).isPresent()) {
            return UserType.STUDENT;
        }
        if (teacherRepository.findByClientId(clientId).isPresent()) {
            return UserType.TEACHER;
        }
        return UserType.UNREGISTERED;
    }
} 