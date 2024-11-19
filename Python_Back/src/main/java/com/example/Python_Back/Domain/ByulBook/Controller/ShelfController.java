package com.example.Python_Back.Domain.ByulBook.Controller;

import com.example.Python_Back.Domain.ByulBook.DTO.ShelfBooksByStatusDTO;
import com.example.Python_Back.Domain.ByulBook.Service.ShelfService;
import com.example.Python_Back.Domain.KaKao.Service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
@Slf4j
@RestController
@RequestMapping("/api/shelf")
public class ShelfController {

    private final AuthService authService;
    private final ShelfService shelfService;

    public ShelfController(AuthService authService, ShelfService shelfService) {
        this.authService = authService;
        this.shelfService = shelfService;
    }

    // 서재에 있는 책들을 상태별로 반환하는 API
    @GetMapping("/books")
    public ResponseEntity<ShelfBooksByStatusDTO> getShelfBooksByStatus(
            @RequestHeader("Authorization") String accessToken) {

        try {
            // 토큰 정보 API를 통해 kakaoUserId 추출
            Long kakaoId = authService.kakaoGetUserIdFromTokenInfo(accessToken);

            if (kakaoId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null); // 유효하지 않은 토큰
            }

            // 상태별 책 정보 가져오기
            ShelfBooksByStatusDTO response = shelfService.getShelfBooksByStatus(kakaoId);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            log.error("서재 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
