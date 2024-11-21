package com.example.Python_Back.Domain.ByulBook.Service;

import com.example.Python_Back.Domain.ByulBook.DTO.ReviewResponseDTO;
import com.example.Python_Back.Domain.ByulBook.Entity.Book;
import com.example.Python_Back.Domain.ByulBook.Entity.BookMark;
import com.example.Python_Back.Domain.ByulBook.Entity.Review;
import com.example.Python_Back.Domain.ByulBook.Entity.ShelfBook;
import com.example.Python_Back.Domain.ByulBook.Repository.BookRepository;
import com.example.Python_Back.Domain.ByulBook.Repository.ReviewRepository;
import com.example.Python_Back.Domain.ByulBook.Repository.ShelfBookRepository;
import com.example.Python_Back.Domain.KaKao.Entity.KakaoUser;
import com.example.Python_Back.Domain.KaKao.Repository.KakaoUserRepository;
import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ShelfBookService {

    private final ShelfBookRepository shelfBookRepository;
    private final ReviewRepository reviewRepository;
    private final BookRepository bookRepository;
    private final KakaoUserRepository kakaoUserRepository;

    public ShelfBookService(ShelfBookRepository shelfBookRepository, ReviewRepository reviewRepository, BookRepository bookRepository, KakaoUserRepository kakaoUserRepository) {
        this.shelfBookRepository = shelfBookRepository;
        this.reviewRepository = reviewRepository;
        this.bookRepository = bookRepository;
        this.kakaoUserRepository = kakaoUserRepository;
    }

    @Transactional
    public ShelfBook startReading(Long kakaoId, Long shelfBookId) {
        ShelfBook shelfBook = getShelfBookForUser(kakaoId, shelfBookId);

        if (shelfBook.getStatus() == ShelfBook.BookStatus.안읽은책 ||
                shelfBook.getStatus() == ShelfBook.BookStatus.또읽을책) {
            shelfBook.setStatus(ShelfBook.BookStatus.덜읽은책);
            return shelfBookRepository.save(shelfBook); // 상태가 변경된 경우 저장
        } else if (shelfBook.getStatus() == ShelfBook.BookStatus.덜읽은책) {
            return shelfBook; // 덜 읽은 책 상태인 경우 저장하지 않고 반환
        } else {
            throw new IllegalArgumentException("독서를 시작할 수 없는 상태입니다.");
        }
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

        if (shelfBook.getStatus() == ShelfBook.BookStatus.또읽을책 ||
                shelfBook.getStatus() == ShelfBook.BookStatus.다읽은책) {
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

    @Transactional
    public ShelfBook updateLastTime(Long kakaoId, Long shelfBookId, String lastTime) {
        // ShelfBook 조회 (사용자와 책의 관계 확인)
        ShelfBook shelfBook = shelfBookRepository.findById(shelfBookId)
                .orElseThrow(() -> new IllegalArgumentException("해당 책을 찾을 수 없습니다."));

        // 추가로 kakaoId 검증 로직 (옵션)
        if (!shelfBook.getShelf().getKakaoUser().getKakaoId().equals(kakaoId)) {
            throw new IllegalArgumentException("권한이 없습니다.");
        }

        // lastTime 업데이트
        shelfBook.setLastTime(lastTime);

        // 저장 및 반환
        return shelfBookRepository.save(shelfBook);
    }


    @Transactional
    public Map<String, Object> getBookDetails(Long kakaoId, Long shelfBookId) {
        // ShelfBook 조회
        ShelfBook shelfBook = shelfBookRepository.findById(shelfBookId)
                .orElseThrow(() -> new IllegalArgumentException("해당 책을 찾을 수 없습니다."));

        // 사용자 권한 확인
        if (!shelfBook.getShelf().getKakaoUser().getKakaoId().equals(kakaoId)) {
            throw new IllegalArgumentException("해당 책에 대한 접근 권한이 없습니다.");
        }

        // 책 정보 반환
        return Map.of(
                "bookId", shelfBook.getBook().getBookId(), // Book ID 추가
                "title", shelfBook.getBook().getTitle(),
                "lastTime", shelfBook.getLastTime() != null ? shelfBook.getLastTime() : "00:00:00"
        );
    }


    // 리뷰 작성
    @Transactional
    public ReviewResponseDTO addReview(Long kakaoId, Long bookId, String content, String reviewImageUrl) {
        // 리뷰 중복 확인
        if (reviewRepository.existsByKakaoUser_KakaoIdAndBook_BookId(kakaoId, bookId)) {
            throw new IllegalArgumentException("이미 리뷰를 작성한 책입니다.");
        }

        // Book 존재 확인
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 책입니다."));

        // KakaoUser DB에서 조회
        KakaoUser kakaoUser = kakaoUserRepository.findByKakaoId(kakaoId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // Review 생성
        Review review = new Review();
        review.setKakaoUser(kakaoUser);
        review.setBook(book);
        review.setContent(content);

        // 리뷰 이미지 URL이 없을 경우 기본값으로 설정
        review.setReviewImageUrl(reviewImageUrl != null ? reviewImageUrl : ""); // 기본값으로 빈 문자열 설정
        review.setCreatedAt(LocalDateTime.now());

        Review savedReview = reviewRepository.save(review);

        // ReviewResponseDTO 반환
        return new ReviewResponseDTO(
                savedReview.getReviewID(),
                savedReview.getBook().getBookId(),
                savedReview.getContent(),
                savedReview.getReviewImageUrl(),
                savedReview.getCreatedAt()
        );
    }




    // 특정 책(Book)에 대한 리뷰 조회
    @Transactional
    public List<String> getReviewsByBook(Long bookId) {
        // 책 ID를 기준으로 리뷰 검색
        List<Review> reviews = reviewRepository.findByBook_BookId(bookId);

        if (reviews.isEmpty()) {
            return List.of();
        }

        // 리뷰 내용을 리스트로 반환
        return reviews.stream()
                .map(Review::getContent)
                .collect(Collectors.toList());
    }





}
