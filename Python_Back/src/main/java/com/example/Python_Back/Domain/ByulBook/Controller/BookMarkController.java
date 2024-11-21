package com.example.Python_Back.Domain.ByulBook.Controller;

import com.example.Python_Back.Domain.ByulBook.DTO.BookMarkDTO;
import com.example.Python_Back.Domain.ByulBook.DTO.BookMarkResponseDTO;
import com.example.Python_Back.Domain.ByulBook.Service.BookMarkService;
import com.example.Python_Back.Domain.KaKao.Service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bookmark")
public class BookMarkController {

    private final BookMarkService bookMarkService;
    private final AuthService authService;

    public BookMarkController(BookMarkService bookMarkService, AuthService authService) {
        this.bookMarkService = bookMarkService;
        this.authService = authService;
    }

    @PostMapping("/add")
    public ResponseEntity<BookMarkResponseDTO> addBookMark(
            @RequestHeader("Authorization") String accessToken,
            @RequestParam Long shelfBookId, // 책 ID
            @RequestParam Integer pageNumber, // 페이지 번호
            @RequestParam String content // 책갈피 내용 (선택적)
    ) {
        try {
            // 책갈피 추가 및 반환
            Long kakaoId = authService.kakaoGetUserIdFromTokenInfo(accessToken);
            BookMarkResponseDTO bookMarkResponseDTO = bookMarkService.addBookMark(kakaoId, shelfBookId,pageNumber, content);
            return ResponseEntity.ok(bookMarkResponseDTO);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // 잘못된 요청
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // 서버 오류
        }
    }


    // 특정 책의 전체 책갈피 조회 API
    @GetMapping("/list")
    public ResponseEntity<List<BookMarkDTO>> getBookMarks(
            @RequestHeader("Authorization") String accessToken,
            @RequestParam Long shelfBookId) {
        try {
            Long kakaoId = authService.kakaoGetUserIdFromTokenInfo(accessToken);
            List<BookMarkDTO> bookMarks = bookMarkService.getBookMarksByKakaoUserAndShelfBook(kakaoId, shelfBookId);
            return ResponseEntity.ok(bookMarks);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // 책갈피 삭제 API
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteBookMark(
            @RequestHeader("Authorization") String accessToken,
            @RequestParam Long bookmarkId) {
        try {
            Long kakaoId = authService.kakaoGetUserIdFromTokenInfo(accessToken);
            bookMarkService.deleteBookMark(kakaoId, bookmarkId);
            return ResponseEntity.ok("책갈피가 성공적으로 삭제되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("책갈피 삭제 중 오류가 발생했습니다.");
        }
    }

}
