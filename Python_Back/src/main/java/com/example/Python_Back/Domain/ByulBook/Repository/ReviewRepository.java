package com.example.Python_Back.Domain.ByulBook.Repository;

import com.example.Python_Back.Domain.ByulBook.Entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByBook_BookId(Long bookId);

    // 특정 사용자와 책에 대한 리뷰가 존재하는지 확인
    boolean existsByKakaoUser_KakaoIdAndBook_BookId(Long kakaoId, Long bookId);
}
