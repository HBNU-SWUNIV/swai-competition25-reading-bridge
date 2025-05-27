package com.dyslexia.dyslexia.controller;

import com.dyslexia.dyslexia.dto.SignUpRequestDto;
import com.dyslexia.dyslexia.dto.SignUpResponseDto;
import com.dyslexia.dyslexia.dto.UserInfoDto;
import com.dyslexia.dyslexia.enums.UserType;
import com.dyslexia.dyslexia.service.UserService;
import com.dyslexia.dyslexia.util.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Parameter;

@Tag(name = "User", description = "사용자 관련 API")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "회원가입 성공",
            content = @Content(schema = @Schema(implementation = SignUpResponseDto.class))),
    })
    @PostMapping("/signup")
    public ResponseEntity<SignUpResponseDto> signUp(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "회원가입 요청 정보",
            required = true,
            content = @Content(schema = @Schema(implementation = SignUpRequestDto.class))
        )
        @RequestBody SignUpRequestDto dto) {
        SignUpResponseDto response = userService.signUp(dto);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "내 정보 조회", description = "인증된 사용자의 정보를 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "내 정보 조회 성공",
            content = @Content(schema = @Schema(implementation = UserInfoDto.class))),
    })
    @GetMapping("/me")
    public ResponseEntity<UserInfoDto> getMyInfo(
        @Parameter(description = "JWT 토큰", required = true)
        @RequestHeader("Authorization") String token
    ) {
        String clientId = jwtTokenProvider.getClientId(token);
        String userType = jwtTokenProvider.getUserType(token);
        UserInfoDto userInfo = userService.getMyInfo(clientId, UserType.valueOf(userType));
        return ResponseEntity.ok(userInfo);
    }
}