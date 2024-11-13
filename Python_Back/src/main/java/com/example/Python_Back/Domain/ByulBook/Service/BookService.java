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
    // 책 제목을 기반으로 감상 리스트를 반환하는 메서드
    public List<String> getReviewsByBookTitle(String title) {
        // 책 제목으로 책 정보 찾기
        Optional<Book> bookOpt = bookRepository.findByTitle(title);

        // 책이 DB에 존재하지 않으면 빈 리스트 반환
        if (bookOpt.isEmpty()) {
            return Collections.emptyList();  // 감상 없음
        }

        // 책이 있으면 해당 책에 대한 감상 리스트 조회
        Book book = bookOpt.get();
        List<Review> reviews = reviewRepository.findByBook_BookId(book.getBookId());

        // 감상평이 없으면 빈 리스트 반환
        if (reviews.isEmpty()) {
            return Collections.emptyList();  // 감상 없음
        }

        // 감상이 있으면 감상 내용을 리스트로 반환
        return reviews.stream()
                .map(Review::getContent)
                .collect(Collectors.toList());
    }
    @Transactional
    // 추천받은 책의 제목을 저장하는 메서드
    public Book saveBookByTitle(String title) {
        // 이미 존재하는 책인지 확인
        Optional<Book> existingBook = bookRepository.findByTitle(title);

        // 존재하면 기존 책 반환, 없으면 새로운 책을 생성하여 저장
        return existingBook.orElseGet(() -> {
            Book newBook = new Book();
            newBook.setTitle(title);
            return bookRepository.save(newBook);  // 새로운 책 저장
        });
    }


}
