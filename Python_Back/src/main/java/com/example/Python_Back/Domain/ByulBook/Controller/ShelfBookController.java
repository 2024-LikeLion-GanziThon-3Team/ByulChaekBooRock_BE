package com.example.Python_Back.Domain.ByulBook.Controller;

import com.example.Python_Back.Domain.ByulBook.DTO.ReviewRequestDTO;
import com.example.Python_Back.Domain.ByulBook.DTO.ReviewResponseDTO;
import com.example.Python_Back.Domain.ByulBook.Entity.ShelfBook;
import com.example.Python_Back.Domain.ByulBook.Service.ShelfBookService;
import com.example.Python_Back.Domain.KaKao.Service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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


    @PutMapping("/update-lasttime/{shelfBookId}")
    public ResponseEntity<Map<String, Object>> updateLastTime(
            @RequestHeader("Authorization") String accessToken,
            @PathVariable Long shelfBookId,
            @RequestParam String lastTime) {
        try {
            Long kakaoId = authService.kakaoGetUserIdFromTokenInfo(accessToken);

            if (kakaoId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                        "error", "유효하지 않은 토큰입니다."
                ));
            }

            ShelfBook updatedShelfBook = shelfBookService.updateLastTime(kakaoId, shelfBookId, lastTime);

            return ResponseEntity.ok(Map.of(
                    "message", "LastTime이 성공적으로 업데이트되었습니다.",
                    "shelfBook", Map.of(
                            "title", updatedShelfBook.getBook().getTitle(),
                            "lastTime", updatedShelfBook.getLastTime() // lastTime은 그대로 String으로 반환
                    )
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "error", "업데이트 실패",
                    "message", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "error", "서버 오류",
                    "message", "LastTime 업데이트 중 문제가 발생했습니다."
            ));
        }
    }

    @GetMapping("/shelfbook-details/{shelfBookId}")
    public ResponseEntity<Map<String, Object>> getBookDetails(
            @RequestHeader("Authorization") String accessToken,
            @PathVariable Long shelfBookId) {
        try {
            // Kakao ID 검증
            Long kakaoId = authService.kakaoGetUserIdFromTokenInfo(accessToken);
            if (kakaoId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                        "error", "유효하지 않은 토큰입니다."
                ));
            }

            // 책 세부 정보 가져오기
            Map<String, Object> bookDetails = shelfBookService.getBookDetails(kakaoId, shelfBookId);
            return ResponseEntity.ok(bookDetails);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "error", "잘못된 요청",
                    "message", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "error", "서버 오류",
                    "message", "책 세부 정보를 가져오는 중 문제가 발생했습니다."
            ));
        }
    }

    // 리뷰 작성 API
    @PostMapping("/review/add")
    public ResponseEntity<Map<String, Object>> addReview(
            @RequestHeader("Authorization") String accessToken,
            @RequestBody ReviewRequestDTO requestDTO) {
        try {
            // Kakao ID 검증
            Long kakaoId = authService.kakaoGetUserIdFromTokenInfo(accessToken);

            // 리뷰 작성
            ReviewResponseDTO response = shelfBookService.addReview(
                    kakaoId,
                    requestDTO.getBookId(),
                    requestDTO.getContent(),
                    requestDTO.getReviewImageUrl() // 선택적 필드 그대로 사용
            );

            // JSON 형태의 응답 생성
            Map<String, Object> responseBody = Map.of(
                    "status", "success",
                    "message", "리뷰가 성공적으로 작성되었습니다.",
                    "review", Map.of(
                            "reviewId", response.getReviewId(),
                            "bookId", response.getBookId(),
                            "content", response.getContent(),
                            "reviewImageUrl", response.getReviewImageUrl(),
                            "createdAt", response.getCreatedAt().toString()
                    )
            );

            return ResponseEntity.ok(responseBody);

        } catch (IllegalArgumentException e) {
            // 잘못된 요청에 대한 JSON 응답
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "status", "fail",
                    "message", e.getMessage()
            ));
        } catch (Exception e) {
            // 서버 오류에 대한 JSON 응답
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "error",
                    "message", "리뷰 작성 중 문제가 발생했습니다."
            ));
        }
    }




    // 특정 책의 리뷰 조회 API
    @GetMapping("/review/book")
    public ResponseEntity<List<String>> getReviewsByBook(
            @RequestParam Long bookId
    ) {
        List<String> reviews = shelfBookService.getReviewsByBook(bookId);
        return ResponseEntity.ok(reviews);
    }






}



