package com.dyslexia.dyslexia.controller;

import com.dyslexia.dyslexia.dto.AuthResponseDto;
import com.dyslexia.dyslexia.service.KakaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Kakao", description = "카카오 로그인 관련 API")
@RestController
@RequestMapping("/kakao")
@RequiredArgsConstructor
public class KakaoController {

    private final KakaoService kakaoService;

    @Operation(summary = "카카오 로그인 콜백", description = "카카오 로그인 후 리다이렉트되는 콜백 URL입니다.")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "로그인 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(
                    implementation = AuthResponseDto.class,
                    description = """
                        로그인 응답 데이터
                        - registered: 회원가입 여부
                        - clientId: 카카오 ID
                        - nickname: 카카오 닉네임
                        - userType: 사용자 타입 (STUDENT/TEACHER/UNREGISTERED)
                        - accessToken: JWT 액세스 토큰 (회원가입된 경우에만)
                        - refreshToken: JWT 리프레시 토큰 (회원가입된 경우에만)
                        """
                )
            )
        ),
    })
    @GetMapping("/callback")
    public ResponseEntity<AuthResponseDto> kakaoCallback(
        @Parameter(description = "카카오 인가 코드", required = true)
        @RequestParam(value = "code") String code) {
        return ResponseEntity.ok(kakaoService.processKakaoCallback(code));
    }
}
