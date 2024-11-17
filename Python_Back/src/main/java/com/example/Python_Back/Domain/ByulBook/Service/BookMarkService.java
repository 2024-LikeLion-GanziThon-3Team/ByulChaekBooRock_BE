package com.example.Python_Back.Domain.ByulBook.Service;

import com.example.Python_Back.Domain.ByulBook.DTO.BookMarkDTO;
import com.example.Python_Back.Domain.ByulBook.Entity.BookMark;
import com.example.Python_Back.Domain.ByulBook.Entity.ShelfBook;
import com.example.Python_Back.Domain.ByulBook.Repository.BookMarkRepository;
import com.example.Python_Back.Domain.ByulBook.Repository.ShelfBookRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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

    // 특정 책의 전체 책갈피 조회
    public List<BookMarkDTO> getBookMarksByShelfBookId(Long shelfBookId) {
        // ShelfBook 존재 여부 확인
        if (!shelfBookRepository.existsById(shelfBookId)) {
            throw new IllegalArgumentException("존재하지 않는 책입니다.");
        }

        // 책갈피 조회 및 DTO 변환
        return bookMarkRepository.findByShelfBook_ShelfBookId(shelfBookId).stream()
                .map(bookMark -> new BookMarkDTO(
                        bookMark.getBookmarkId(),
                        bookMark.getShelfBook().getShelfBookId(), // ShelfBook ID 추가
                        bookMark.getPageNumber(),
                        bookMark.getContent(),
                        bookMark.getCreatedAt(),
                        bookMark.getUpdateAt()
                ))
                .collect(Collectors.toList());
    }

    // 책갈피 삭제
    public void deleteBookMark(Long bookmarkId) {
        if (!bookMarkRepository.existsById(bookmarkId)) {
            throw new IllegalArgumentException("존재하지 않는 책갈피입니다.");
        }
        bookMarkRepository.deleteById(bookmarkId);
    }
}
