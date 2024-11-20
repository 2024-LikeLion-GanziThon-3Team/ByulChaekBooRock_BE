package com.example.Python_Back.Domain.ByulBook.Controller;

import com.example.Python_Back.Domain.ByulBook.Entity.ShelfBook;
import com.example.Python_Back.Domain.ByulBook.Service.ShelfBookService;
import com.example.Python_Back.Domain.KaKao.Service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
@Slf4j
@RestController
@RequestMapping("/api/shelf")
public class ShelfBookController {

    private final ShelfBookService shelfBookService;
    private final AuthService authService;

    public ShelfBookController(ShelfBookService shelfBookService, AuthService authService) {
        this.shelfBookService = shelfBookService;
        this.authService = authService;
    }

    @PutMapping("/start-reading/{shelfBookId}")
    public ResponseEntity<Map<String, Object>> startReading(
            @RequestHeader("Authorization") String accessToken,
            @PathVariable Long shelfBookId) {
        return handleStatusChange(accessToken, shelfBookId, "startReading");
    }

    @PutMapping("/finish-reading/{shelfBookId}")
    public ResponseEntity<Map<String, Object>> finishReading(
            @RequestHeader("Authorization") String accessToken,
            @PathVariable Long shelfBookId) {
        return handleStatusChange(accessToken, shelfBookId, "finishReading");
    }

    @PutMapping("/mark-for-reread/{shelfBookId}")
    public ResponseEntity<Map<String, Object>> markForReread(
            @RequestHeader("Authorization") String accessToken,
            @PathVariable Long shelfBookId) {
        return handleStatusChange(accessToken, shelfBookId, "markForReread");
    }

    @PutMapping("/restart-reading/{shelfBookId}")
    public ResponseEntity<Map<String, Object>> restartReading(
            @RequestHeader("Authorization") String accessToken,
            @PathVariable Long shelfBookId) {
        return handleStatusChange(accessToken, shelfBookId, "restartReading");
    }

    private ResponseEntity<Map<String, Object>> handleStatusChange(String accessToken, Long shelfBookId, String action) {
        try {
            Long kakaoId = authService.kakaoGetUserIdFromTokenInfo(accessToken);

            if (kakaoId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                        "error", "유효하지 않은 토큰입니다."
                ));
            }

            ShelfBook updatedShelfBook;

            switch (action) {
                case "startReading" -> updatedShelfBook = shelfBookService.startReading(kakaoId, shelfBookId);
                case "finishReading" -> updatedShelfBook = shelfBookService.finishReading(kakaoId, shelfBookId);
                case "markForReread" -> updatedShelfBook = shelfBookService.markForReread(kakaoId, shelfBookId);
                case "restartReading" -> updatedShelfBook = shelfBookService.restartReading(kakaoId, shelfBookId);
                default -> throw new IllegalArgumentException("잘못된 동작 요청입니다.");
            }

            return ResponseEntity.ok(Map.of(
                    "message", "상태가 변경되었습니다.",
                    "shelfBook", Map.of(
                            "title", updatedShelfBook.getBook().getTitle(),
                            "status", updatedShelfBook.getStatus().name()
                    )
            ));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "error", "상태 변경 실패",
                    "message", e.getMessage()
            ));
        } catch (Exception e) {
            log.error("상태 변경 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "error", "서버 오류",
                    "message", "상태 변경 중 문제가 발생했습니다."
            ));
        }
    }
}



