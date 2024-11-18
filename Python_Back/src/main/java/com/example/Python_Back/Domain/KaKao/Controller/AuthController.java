package com.example.Python_Back.Domain.KaKao.Controller;

import com.example.Python_Back.Domain.KaKao.Entity.KakaoUser;
import com.example.Python_Back.Domain.KaKao.Service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
//@RequiredArgsConstructor
@RequestMapping("/auth")
@Slf4j
public class AuthController {

    private final AuthService authService;
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // 프론트와 연결 시  컨트롤러 수정해서 프론트에서 코드를 보내고 그걸 받아서 토큰 발급 진행해야함
    @GetMapping("/kakaoLogin")
    public ResponseEntity<Map<String, Object>> kakaoLogin(@RequestParam("code") String code) {
        try {
            // 1단계: 전달받은 인가 코드를 사용하여 Kakao에서 액세스 토큰 발급
            String accessToken = authService.kakaoGetAccessViaCode(code);

            // 2단계: 액세스 토큰을 사용하여 Kakao 사용자 정보 조회 및 서재 생성
            KakaoUser kakaoUser = authService.kakaoGetUserInfoViaAccessToken(accessToken);

            // JSON 형태의 응답 생성
            Map<String, Object> response = new HashMap<>();
            response.put("accessToken", accessToken);
            response.put("kakaoId", kakaoUser.getKakaoId());
            response.put("nickname", kakaoUser.getNickname());
            response.put("connectedAt", kakaoUser.getConnectedAt());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // 실패 시에도 Map<String, Object> 사용
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "인증 실패");
            errorResponse.put("message", "유효한 code가 아닙니다.");

            return ResponseEntity.status(500).body(errorResponse);
        }
    }


    @GetMapping("/logout")
    public void kakaoLogout(@RequestParam("token") String token) {
        authService.kakaoLogout(token);
    }

    // 카카오 회원 탈퇴 처리
    @GetMapping("/unlink")
    public ResponseEntity<Map<String, Object>> kakaoUnlink(@RequestHeader("Authorization") String token) {
        try {
            // 1. 헤더로부터 Bearer 토큰을 추출 (앞의 "Bearer " 부분 제거)
            String accessToken = token.replace("Bearer ", "");

            // 2. 액세스 토큰을 사용해 카카오 계정 연결 해제 요청
            String response = authService.kakaoUnlink(accessToken);

            // 3. JSON 응답 생성
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("status", "success");
            responseBody.put("message", "회원 탈퇴가 성공적으로 처리되었습니다.");
            responseBody.put("kakaoResponse", response);

            return ResponseEntity.ok(responseBody);
        } catch (Exception e) {
            log.error("탈퇴 실패: ", e);

            // 4. 실패 시 JSON 형태의 에러 응답 반환
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "회원 탈퇴 중 오류가 발생했습니다.");
            errorResponse.put("error", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }








}
