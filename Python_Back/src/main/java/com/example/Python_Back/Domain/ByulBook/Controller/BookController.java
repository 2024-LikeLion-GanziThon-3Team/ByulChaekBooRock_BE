package com.example.Python_Back.Domain.ByulBook.Controller;

import com.example.Python_Back.Domain.ByulBook.DTO.BookRequestDTO;
import com.example.Python_Back.Domain.ByulBook.DTO.BookResponseDTO;
import com.example.Python_Back.Domain.ByulBook.Entity.ShelfBook;
import com.example.Python_Back.Domain.ByulBook.Service.BookService;
import com.example.Python_Back.Domain.ByulBook.Service.ShelfService;
import com.example.Python_Back.Domain.KaKao.Service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

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


    // 책 제목을 받아 감상 리스트를 반환하는 API
    // 책 제목 리스트를 받아서 각 책의 감상 리스트를 반환
    @PostMapping("/reviews")
    public ResponseEntity<Map<String, Object>> getMultipleBookReviews(
            @RequestHeader("Authorization") String accessToken,
            @RequestBody BookRequestDTO request) {

        // 토큰 정보 API를 통해 kakaoUserId 추출
        Long kakaoId = authService.kakaoGetUserIdFromTokenInfo(accessToken);

        if (kakaoId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);  // 유효하지 않은 토큰이면 401 반환
        }

        List<String> bookTitles = request.getTitles();  // 프론트엔드에서 받은 책 제목 리스트
        List<BookResponseDTO> responseList = new ArrayList<>();

        // 각 책에 대해 감상 리스트를 조회
        for (String title : bookTitles) {
            List<String> reviews = bookService.getReviewsByBookTitle(title);

            BookResponseDTO response = new BookResponseDTO();
            response.setTitle(title);

            if (reviews.isEmpty()) {
                response.setReviews(Arrays.asList("감상이 없습니다."));
            } else {
                response.setReviews(reviews);
            }

            responseList.add(response);
        }
        // 응답에 kakaoId와 리뷰 리스트를 포함
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("kakaoId", kakaoId);
        responseBody.put("reviews", responseList);

        return ResponseEntity.ok(responseBody);
    }


    // 서재에 추천받은 책을 추가하는 API
    @PostMapping("/add-to-shelf")
    public ResponseEntity<String> addBookToShelf(
            @RequestHeader("Authorization") String accessToken,
            @RequestParam String title) {

        // 토큰 정보 API를 통해 kakaoUserId 추출
        Long kakaoId = authService.kakaoGetUserIdFromTokenInfo(accessToken);

        if (kakaoId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 토큰입니다.");
        }

        // 책을 서재에 추가 (기본 상태는 '안읽은책'으로 설정됨)
        shelfService.addBookToShelf(kakaoId, title);
        return ResponseEntity.ok("책이 서재에 추가되었습니다.");
    }

}
