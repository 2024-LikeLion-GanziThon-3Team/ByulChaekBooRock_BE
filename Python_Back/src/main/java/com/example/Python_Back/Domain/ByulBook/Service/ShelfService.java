package com.example.Python_Back.Domain.ByulBook.Service;

import com.example.Python_Back.Domain.ByulBook.Entity.Book;
import com.example.Python_Back.Domain.ByulBook.Entity.Shelf;
import com.example.Python_Back.Domain.ByulBook.Entity.ShelfBook;
import com.example.Python_Back.Domain.ByulBook.Repository.ShelfRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ShelfService {
    private final ShelfRepository shelfRepository;
    private final BookService bookService;

    public ShelfService(ShelfRepository shelfRepository, BookService bookService) {
        this.shelfRepository = shelfRepository;
        this.bookService = bookService;
    }

    @Transactional
    public ShelfBook addBookToShelf(Long kakaoId, String title) {
        // 사용자의 서재 찾기
        Shelf shelf = shelfRepository.findByKakaoUser_KakaoId(kakaoId)
                .orElseThrow(() -> new IllegalArgumentException("서재를 찾을 수 없습니다."));

        // 책을 저장하거나 기존 책 반환
        Book book = bookService.saveBookByTitle(title);

        // 서재에 이미 있는 책인지 확인
        boolean alreadyExists = shelf.getShelfBooks().stream()
                .anyMatch(shelfBook -> shelfBook.getBook().getBookId().equals(book.getBookId()));

        if (alreadyExists) {
            throw new IllegalArgumentException("해당 책은 이미 서재에 추가되어 있습니다.");
        }

        // ShelfBook 생성 및 서재에 추가 - 기본 상태를 '안읽은책'으로 설정
        ShelfBook shelfBook = new ShelfBook();
        shelfBook.setShelf(shelf);
        shelfBook.setBook(book);
        shelfBook.setStatus(ShelfBook.BookStatus.안읽은책);  // 기본 상태 설정

        shelf.getShelfBooks().add(shelfBook);
        return shelfBook;
    }
}
