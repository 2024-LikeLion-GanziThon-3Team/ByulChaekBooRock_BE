package com.example.Python_Back.Domain.ByulBook.Controller;

import com.example.Python_Back.Domain.ByulBook.Service.BookMarkService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookmark")
public class BookMarkController {

    private final BookMarkService bookMarkService;

    public BookMarkController(BookMarkService bookMarkService) {
        this.bookMarkService = bookMarkService;
    }

    // 책갈피 추가 API
    @PostMapping("/add")
    public ResponseEntity<String> addBookMark(
            @RequestHeader("Authorization") String accessToken,
            @RequestParam Long shelfBookId, // 책 ID
            @RequestParam Integer pageNumber, // 페이지 번호
            @RequestParam  String content // 책갈피 내용 (선택적)
    ) {
        try {
            bookMarkService.addBookMark(shelfBookId, pageNumber, content);
            return ResponseEntity.ok("책갈피가 성공적으로 추가되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("책갈피 추가 중 오류가 발생했습니다.");
        }
    }
}
