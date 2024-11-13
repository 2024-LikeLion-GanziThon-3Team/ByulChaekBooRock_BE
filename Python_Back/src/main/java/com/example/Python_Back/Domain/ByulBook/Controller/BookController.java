package com.example.Python_Back.Domain.ByulBook.Controller;

import com.example.Python_Back.Domain.ByulBook.DTO.BookRequestDTO;
import com.example.Python_Back.Domain.ByulBook.DTO.BookResponseDTO;
import com.example.Python_Back.Domain.ByulBook.Service.BookService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/recommend")
public class BookController {
    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }


    // 책 제목을 받아 감상 리스트를 반환하는 API
    @PostMapping("/reviews")
    public ResponseEntity<BookResponseDTO> getBookReviews(@RequestBody BookRequestDTO request) {
        String bookTitle = request.getTitle();  // BookRequestDTO로 받은 책 제목
        // 책 제목을 데이터베이스에 저장
        //bookService.saveBookByTitle(bookTitle);

        // 책 존재 여부 확인 및 감상 리스트 반환 로직 수행
        List<String> reviews = bookService.getReviewsByBookTitle(bookTitle);

        // BookResponseDTO 객체 생성
        BookResponseDTO response = new BookResponseDTO();
        response.setTitle(bookTitle);  // 책 제목 설정

        if (reviews.isEmpty()) {
            response.setReviews(Arrays.asList(""));  // 감상이 없으면 빈 리스트 반환
        } else {
            response.setReviews(reviews);  // 감상 리스트 설정
        }

        // 책 제목과 감상 리스트가 담긴 DTO 반환
        return ResponseEntity.ok(response);
    }
}
