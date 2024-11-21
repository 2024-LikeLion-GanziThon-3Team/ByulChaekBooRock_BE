package com.example.Python_Back.Domain.ByulBook.Service;

import com.example.Python_Back.Domain.ByulBook.DTO.BookMarkDTO;
import com.example.Python_Back.Domain.ByulBook.DTO.BookMarkResponseDTO;
import com.example.Python_Back.Domain.ByulBook.Entity.BookMark;
import com.example.Python_Back.Domain.ByulBook.Entity.ShelfBook;
import com.example.Python_Back.Domain.ByulBook.Repository.BookMarkRepository;
import com.example.Python_Back.Domain.ByulBook.Repository.ShelfBookRepository;
import com.example.Python_Back.Domain.KaKao.Entity.KakaoUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
    public BookMarkResponseDTO addBookMark(Long kakaoId,Long shelfBookId, Integer pageNumber, String content) {
        // ShelfBook이 존재하는지 확인
        ShelfBook shelfBook = shelfBookRepository.findById(shelfBookId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 책입니다."));

        KakaoUser kakaoUser = new KakaoUser();
        kakaoUser.setKakaoId(kakaoId);

        // BookMark 생성 및 저장
        BookMark bookMark = new BookMark();
        bookMark.setKakaoUser(kakaoUser);
        bookMark.setShelfBook(shelfBook);
        bookMark.setPageNumber(pageNumber);
        bookMark.setContent(content);
        bookMark.setCreatedAt(LocalDateTime.now());


        BookMark savedBookMark = bookMarkRepository.save(bookMark);

        // BookMark 엔티티를 BookMarkResponseDTO로 변환 후 반환
        return new BookMarkResponseDTO(
                savedBookMark.getBookmarkId(),
                savedBookMark.getPageNumber(),
                savedBookMark.getContent(),
                savedBookMark.getCreatedAt()

        );
    }

    // 특정 사용자의 책갈피 목록 조회
    @Transactional
    public List<BookMarkDTO> getBookMarksByKakaoUserAndShelfBook(Long kakaoId, Long shelfBookId) {
        // ShelfBook 존재 여부 확인
        if (!shelfBookRepository.existsById(shelfBookId)) {
            throw new IllegalArgumentException("존재하지 않는 책입니다.");
        }

        // 사용자 기반 책갈피 조회 및 DTO 변환
        return bookMarkRepository.findByKakaoUser_KakaoIdAndShelfBook_ShelfBookId(kakaoId, shelfBookId).stream()
                .map(bookMark -> new BookMarkDTO(
                        bookMark.getBookmarkId(),
                        bookMark.getShelfBook().getShelfBookId(),
                        bookMark.getPageNumber(),
                        bookMark.getContent(),
                        bookMark.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    // 책갈피 삭제
    public void deleteBookMark(Long kakaoId, Long bookmarkId) {
        // 책갈피 존재 여부 확인
        BookMark bookMark = bookMarkRepository.findById(bookmarkId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 책갈피입니다."));

        // 삭제 권한 확인
        if (!bookMark.getKakaoUser().getKakaoId().equals(kakaoId)) {
            throw new IllegalArgumentException("권한이 없습니다.");
        }

        bookMarkRepository.deleteByKakaoUser_KakaoIdAndBookmarkId(kakaoId, bookmarkId);
    }
}
