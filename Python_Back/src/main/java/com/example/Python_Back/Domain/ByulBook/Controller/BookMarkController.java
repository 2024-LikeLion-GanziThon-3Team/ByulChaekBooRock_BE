package com.example.Python_Back.Domain.ByulBook.Controller;

import com.example.Python_Back.Domain.ByulBook.DTO.BookMarkResponseDTO;
import com.example.Python_Back.Domain.ByulBook.Service.BookMarkService;
import com.example.Python_Back.Domain.KaKao.Service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookmark")
public class BookMarkController {

    private final BookMarkService bookMarkService;
    private final AuthService authService;

    public BookMarkController(BookMarkService bookMarkService, AuthService authService) {
        this.bookMarkService = bookMarkService;
        this.authService = authService;
    }

    // 책갈피 추가 API
    @PostMapping("/add")
    public ResponseEntity<BookMarkResponseDTO> addBookMark(
            @RequestHeader("Authorization") String accessToken, // 사용자 인증 토큰
            @RequestParam Long shelfBookId,                     // 서재의 책 ID
            @RequestParam Integer pageNumber,                   // 페이지 번호
            @RequestParam String content                        // 책갈피 내용
    ) {
        try {
            // 토큰 정보 API를 통해 kakaoUserId 추출
            Long kakaoId = authService.kakaoGetUserIdFromTokenInfo(accessToken);

            if (kakaoId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null); // 유효하지 않은 토큰
            }

            // 책갈피 추가 및 반환
            BookMarkResponseDTO bookMarkResponse = bookMarkService.addBookMarkForUser(kakaoId, shelfBookId, pageNumber, content);
            return ResponseEntity.ok(bookMarkResponse);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
