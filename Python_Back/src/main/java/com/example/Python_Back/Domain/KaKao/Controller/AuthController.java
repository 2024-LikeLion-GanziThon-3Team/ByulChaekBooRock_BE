package com.example.Python_Back.Domain.KaKao.Controller;

import com.example.Python_Back.Domain.KaKao.Entity.KakaoUser;
import com.example.Python_Back.Domain.KaKao.Service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
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
    public ResponseEntity<String> kakaoLogin(@RequestParam("code") String code) {
        try {
            // 1단계: 전달받은 인가 코드를 사용하여 Kakao에서 액세스 토큰 발급
            String accessToken = authService.kakaoGetAccessViaCode(code);

            // 2단계: 액세스 토큰을 사용하여 Kakao 사용자 정보 조회 및 서재 생성
            KakaoUser kakaoUser = authService.kakaoGetUserInfoViaAccessToken(accessToken);

            // 헤더에 토큰을 포함시켜 반환
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken);

            String response = "사용자 인증 성공: " + kakaoUser.getNickname();

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("인증 실패: 유효한 code가 아닙니다.");
        }
    }
    @GetMapping("/logout")
    public void kakaoLogout(@RequestParam("token") String token) {
        authService.kakaoLogout(token);
    }

    // 카카오 회원 탈퇴 처리
    @GetMapping("/unlink")
    public ResponseEntity<String> kakaoUnlink(@RequestHeader("Authorization") String token) {
        try {
            // 1. 헤더로부터 Bearer 토큰을 추출 (앞의 "Bearer " 부분 제거)
            String accessToken = token.replace("Bearer ", "");

            // 2. 액세스 토큰을 사용해 카카오 계정 연결 해제 요청
            String response = authService.kakaoUnlink(accessToken);

            // 3. 로그에 탈퇴 요청 응답 기록
            log.info("탈퇴 요청 응답: {}", response);

            // 4. 성공 시 탈퇴 완료 메시지를 반환
            return ResponseEntity.ok("탈퇴 완료: " + response);
        } catch (Exception e) {
            log.error("탈퇴 실패: ", e);
            // 5. 실패 시 500 에러와 함께 실패 메시지를 반환
            return ResponseEntity.status(500).body("탈퇴 실패");
        }
    }

    // 액세스 토큰 정보 확인
    @GetMapping("/tokenInfo")
    public ResponseEntity<Object> getTokenInfo(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            // "Bearer " 접두어 제거
            String accessToken = authorizationHeader.replace("Bearer ", "");

            // 서비스 호출
            Map<String, Object> tokenInfo = authService.getTokenInfo(accessToken);

            // 응답 반환
            return ResponseEntity.ok(tokenInfo);
        } catch (Exception e) {
            log.error("토큰 정보 확인 실패: ", e);
            return ResponseEntity.status(500).body("토큰 정보 확인 실패: " + e.getMessage());
        }
    }






}
