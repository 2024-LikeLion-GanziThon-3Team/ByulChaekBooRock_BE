package com.example.Python_Back.Domain.ByulBook.Service;

import com.example.Python_Back.Domain.ByulBook.Entity.BookMark;
import com.example.Python_Back.Domain.ByulBook.Entity.ShelfBook;
import com.example.Python_Back.Domain.ByulBook.Repository.BookMarkRepository;
import com.example.Python_Back.Domain.ByulBook.Repository.ShelfBookRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
@Service
public class BookMarkService {
    private final BookMarkRepository bookMarkRepository;
    private final ShelfBookRepository shelfBookRepository;

    public BookMarkService(BookMarkRepository bookMarkRepository, ShelfBookRepository shelfBookRepository) {
        this.bookMarkRepository = bookMarkRepository;
        this.shelfBookRepository = shelfBookRepository;
    }

    // 책갈피 추가
    public BookMark addBookMark(Long shelfBookId, Integer pageNumber, String content) {
        // ShelfBook이 존재하는지 확인
        ShelfBook shelfBook = shelfBookRepository.findById(shelfBookId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 책입니다."));

        // BookMark 생성 및 저장
        BookMark bookMark = new BookMark();
        bookMark.setShelfBook(shelfBook);
        bookMark.setPageNumber(pageNumber);
        bookMark.setContent(content);
        bookMark.setCreatedAt(LocalDateTime.now());
        bookMark.setUpdateAt(LocalDateTime.now());

        return bookMarkRepository.save(bookMark);
    }
}
