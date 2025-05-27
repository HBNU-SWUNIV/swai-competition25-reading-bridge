package com.dyslexia.dyslexia.config.oauth;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
public class OAuth2Attributes {

    private Map<String, Object> attributes;
    private String nameAttributeKey;
    private String id;
    private String nickname;

    @Builder
    public OAuth2Attributes(Map<String, Object> attributes, String nameAttributeKey, String id,
        String nickname) {
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
        this.id = id;
        this.nickname = nickname;
    }

    public static OAuth2Attributes of(String registrationId, String userNameAttributeName,
        Map<String, Object> attributes) {
        if ("kakao".equals(registrationId)) {
            return ofKakao(userNameAttributeName, attributes);
        }
        throw new IllegalArgumentException("지원하지 않는 소셜 로그인입니다.");
    }

    @SuppressWarnings("unchecked")
    private static OAuth2Attributes ofKakao(String userNameAttributeName,
        Map<String, Object> attributes) {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> kakaoProfile = (Map<String, Object>) kakaoAccount.get("profile");

        return OAuth2Attributes.builder().id(attributes.get(userNameAttributeName).toString())
            .nickname((String) kakaoProfile.get("nickname")).attributes(attributes)
            .nameAttributeKey(userNameAttributeName).build();
    }
}