package com.example.Python_Back.Domain.ByulBook.Controller;

import com.example.Python_Back.Domain.ByulBook.DTO.BookRequestDTO;
import com.example.Python_Back.Domain.ByulBook.DTO.BookResponseDTO;
import com.example.Python_Back.Domain.ByulBook.Entity.ShelfBook;
import com.example.Python_Back.Domain.ByulBook.Service.BookService;
import com.example.Python_Back.Domain.ByulBook.Service.ShelfService;
import com.example.Python_Back.Domain.KaKao.Service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
@Slf4j
@RestController
@RequestMapping("/api/recommend")
public class BookController {
    private final BookService bookService;
    private final ShelfService shelfService;
    private final AuthService authService;

    public BookController(BookService bookService, ShelfService shelfService, AuthService authService) {
        this.bookService = bookService;
        this.shelfService = shelfService;
        this.authService = authService;
    }


    @PostMapping("/reviews")
    public ResponseEntity<Map<String, Object>> getMultipleBookReviews(
            @RequestHeader("Authorization") String accessToken,
            @RequestBody BookRequestDTO request) {

        try {
            // 토큰 정보 API를 통해 kakaoUserId 추출
            Long kakaoId = authService.kakaoGetUserIdFromTokenInfo(accessToken);

            if (kakaoId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                        "error", "유효하지 않은 토큰입니다."
                ));
            }

            List<BookRequestDTO.BookInfo> bookInfos = request.getBooks(); // 프론트엔드에서 받은 책 정보 리스트 (제목, 표지 URL 포함)
            List<BookResponseDTO> responseList = new ArrayList<>();

            // 각 책 정보 처리
            for (BookRequestDTO.BookInfo bookInfo : bookInfos) {
                String title = bookInfo.getTitle();
                String coverImageUrl = bookInfo.getCoverImageUrl();

                // 책 저장 또는 기존 책 반환 (BookService 활용)
                bookService.saveBookByTitleAndCover(title, coverImageUrl);

                // 리뷰 조회
                List<String> reviews = bookService.getReviewsByBookTitle(title);

                // 응답 DTO 생성
                BookResponseDTO response = new BookResponseDTO();
                response.setTitle(title);
                response.setCoverImageUrl(coverImageUrl); // 요청에서 받은 표지 URL
                response.setReviews(reviews.isEmpty() ? Arrays.asList("감상이 없습니다.") : reviews);

                responseList.add(response);
            }

            // 응답에 kakaoId와 리뷰 리스트를 포함
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("kakaoId", kakaoId);
            responseBody.put("reviews", responseList);

            return ResponseEntity.ok(responseBody);

        } catch (Exception e) {
            log.error("리뷰 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "error", "서버 오류",
                    "message", "리뷰 조회 중 문제가 발생했습니다."
            ));
        }
    }


    @PostMapping("/add-to-shelf")
    public ResponseEntity<Map<String, Object>> addBookToShelf(
            @RequestHeader("Authorization") String accessToken,
            @RequestParam String title,
            @RequestParam(required = false) String coverImageUrl) {

        try {
            // 토큰 정보 API를 통해 kakaoUserId 추출
            Long kakaoId = authService.kakaoGetUserIdFromTokenInfo(accessToken);

            if (kakaoId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                        "error", "유효하지 않은 토큰입니다."
                ));
            }

            // 책을 서재에 추가
            ShelfBook addedShelfBook = shelfService.addBookToShelf(kakaoId, title);

            // JSON 응답 생성
            Map<String, Object> response = new HashMap<>();
            response.put("message", "책이 서재에 추가되었습니다.");

            Map<String, Object> shelfBookInfo = new HashMap<>();
            shelfBookInfo.put("title", addedShelfBook.getBook().getTitle());

            response.put("shelfBook", shelfBookInfo);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "error", "책 추가 실패",
                    "message", e.getMessage()
            ));
        } catch (Exception e) {
            log.error("책 추가 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "error", "서버 오류",
                    "message", "책 추가 중 문제가 발생했습니다."
            ));
        }
    }



}
