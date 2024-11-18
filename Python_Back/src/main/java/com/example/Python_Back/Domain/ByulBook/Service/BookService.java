package com.example.Python_Back.Domain.ByulBook.Service;

import com.example.Python_Back.Domain.ByulBook.Entity.Book;
import com.example.Python_Back.Domain.ByulBook.Entity.Review;
import com.example.Python_Back.Domain.ByulBook.Repository.BookRepository;
import com.example.Python_Back.Domain.ByulBook.Repository.ReviewRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BookService {
    private final BookRepository bookRepository;
    private final ReviewRepository reviewRepository;


    public BookService(BookRepository bookRepository, ReviewRepository reviewRepository) {
        this.bookRepository = bookRepository;
        this.reviewRepository = reviewRepository;
    }

    @Transactional
    public Book saveBookByTitle(String title) {
        // 이미 존재하는 책인지 확인 후 저장
        return bookRepository.findByTitle(title).orElseGet(() -> {
            Book newBook = new Book();
            newBook.setTitle(title);
            return bookRepository.save(newBook); // 새로운 책 저장
        });
    }

    @Transactional(readOnly = true)
    public List<String> getReviewsByBookTitle(String title) {
        // 책 제목으로 책 정보 찾기
        Optional<Book> bookOpt = bookRepository.findByTitle(title);

        // 책이 존재하지 않으면 빈 리스트 반환
        if (bookOpt.isEmpty()) {
            return Collections.emptyList(); // 감상 없음
        }

        // 책에 대한 리뷰 리스트 조회
        Book book = bookOpt.get();
        List<Review> reviews = reviewRepository.findByBook_BookId(book.getBookId());

        // 리뷰가 없으면 빈 리스트 반환
        if (reviews.isEmpty()) {
            return Collections.emptyList(); // 감상 없음
        }

        // 리뷰 내용을 리스트로 반환
        return reviews.stream()
                .map(Review::getContent)
                .collect(Collectors.toList());
    }


}
