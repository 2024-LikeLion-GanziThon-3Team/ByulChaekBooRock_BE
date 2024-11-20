package com.example.Python_Back.Domain.ByulBook.Service;

import com.example.Python_Back.Domain.ByulBook.Entity.ShelfBook;
import com.example.Python_Back.Domain.ByulBook.Repository.ShelfBookRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class ShelfBookService {

    private final ShelfBookRepository shelfBookRepository;

    public ShelfBookService(ShelfBookRepository shelfBookRepository) {
        this.shelfBookRepository = shelfBookRepository;
    }

    @Transactional
    public ShelfBook startReading(Long kakaoId, Long shelfBookId) {
        ShelfBook shelfBook = getShelfBookForUser(kakaoId, shelfBookId);

        if (shelfBook.getStatus() == ShelfBook.BookStatus.안읽은책 ||
                shelfBook.getStatus() == ShelfBook.BookStatus.또읽을책) {
            shelfBook.setStatus(ShelfBook.BookStatus.덜읽은책);
        } else {
            throw new IllegalArgumentException("독서를 시작할 수 없는 상태입니다.");
        }

        return shelfBookRepository.save(shelfBook);
    }

    @Transactional
    public ShelfBook finishReading(Long kakaoId, Long shelfBookId) {
        ShelfBook shelfBook = getShelfBookForUser(kakaoId, shelfBookId);

        if (shelfBook.getStatus() == ShelfBook.BookStatus.덜읽은책) {
            shelfBook.setStatus(ShelfBook.BookStatus.다읽은책);
        } else {
            throw new IllegalArgumentException("독서를 완료할 수 없는 상태입니다.");
        }

        return shelfBookRepository.save(shelfBook);
    }

    @Transactional
    public ShelfBook markForReread(Long kakaoId, Long shelfBookId) {
        ShelfBook shelfBook = getShelfBookForUser(kakaoId, shelfBookId);

        if (shelfBook.getStatus() == ShelfBook.BookStatus.다읽은책) {
            shelfBook.setStatus(ShelfBook.BookStatus.또읽을책);
        } else {
            throw new IllegalArgumentException("다시 읽기 상태로 변경할 수 없습니다.");
        }

        return shelfBookRepository.save(shelfBook);
    }

    @Transactional
    public ShelfBook restartReading(Long kakaoId, Long shelfBookId) {
        ShelfBook shelfBook = getShelfBookForUser(kakaoId, shelfBookId);

        if (shelfBook.getStatus() == ShelfBook.BookStatus.또읽을책) {
            shelfBook.setStatus(ShelfBook.BookStatus.덜읽은책);
        } else {
            throw new IllegalArgumentException("독서를 다시 시작할 수 없는 상태입니다.");
        }

        return shelfBookRepository.save(shelfBook);
    }

    // 공통 로직: ShelfBook 조회 및 사용자 검증
    private ShelfBook getShelfBookForUser(Long kakaoId, Long shelfBookId) {
        ShelfBook shelfBook = shelfBookRepository.findById(shelfBookId)
                .orElseThrow(() -> new IllegalArgumentException("책을 찾을 수 없습니다."));

        if (!shelfBook.getShelf().getKakaoUser().getKakaoId().equals(kakaoId)) {
            throw new IllegalArgumentException("해당 사용자의 책이 아닙니다.");
        }

        return shelfBook;
    }
}
