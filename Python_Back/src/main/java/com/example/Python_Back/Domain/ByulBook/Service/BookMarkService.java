package com.example.Python_Back.Domain.ByulBook.Service;

import com.example.Python_Back.Domain.ByulBook.DTO.BookMarkResponseDTO;
import com.example.Python_Back.Domain.ByulBook.DTO.ShelfBookResponseDTO;
import com.example.Python_Back.Domain.ByulBook.Entity.BookMark;
import com.example.Python_Back.Domain.ByulBook.Entity.Shelf;
import com.example.Python_Back.Domain.ByulBook.Entity.ShelfBook;
import com.example.Python_Back.Domain.ByulBook.Repository.BookMarkRepository;
import com.example.Python_Back.Domain.ByulBook.Repository.ShelfBookRepository;
import com.example.Python_Back.Domain.ByulBook.Repository.ShelfRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
@Service
public class BookMarkService {

    private final BookMarkRepository bookMarkRepository;
    private final ShelfBookRepository shelfBookRepository;
    private final ShelfRepository shelfRepository;

    public BookMarkService(BookMarkRepository bookMarkRepository, ShelfBookRepository shelfBookRepository,
                           ShelfRepository shelfRepository) {
        this.bookMarkRepository = bookMarkRepository;
        this.shelfBookRepository = shelfBookRepository;
        this.shelfRepository = shelfRepository;
    }

    @Transactional
    public BookMarkResponseDTO addBookMarkForUser(Long kakaoId, Long shelfBookId, Integer pageNumber, String content) {
        // 사용자의 서재 조회
        Shelf shelf = shelfRepository.findByKakaoUser_KakaoId(kakaoId)
                .orElseThrow(() -> new IllegalArgumentException("서재를 찾을 수 없습니다."));

        // ShelfBook이 해당 서재에 있는지 확인
        ShelfBook shelfBook = shelfBookRepository.findById(shelfBookId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 책입니다."));

        if (!shelf.getShelfBooks().contains(shelfBook)) {
            throw new IllegalArgumentException("해당 책은 사용자의 서재에 없습니다.");
        }

        // BookMark 생성 및 저장
        BookMark bookMark = new BookMark();
        bookMark.setShelfBook(shelfBook);
        bookMark.setPageNumber(pageNumber);
        bookMark.setContent(content);
        bookMark.setCreatedAt(LocalDateTime.now());
        bookMark.setUpdateAt(LocalDateTime.now());

        BookMark savedBookMark = bookMarkRepository.save(bookMark);

        // BookMarkResponseDTO 생성 및 반환
        return new BookMarkResponseDTO(
                savedBookMark.getBookmarkId(),
                savedBookMark.getPageNumber(),
                savedBookMark.getContent(),
                savedBookMark.getCreatedAt(),
                savedBookMark.getUpdateAt()

        );
    }
}
