package com.example.Python_Back.Domain.ByulBook.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class ReviewResponseDTO {
    private Long reviewId;           // 리뷰 ID
    private Long bookId;             // 책 ID
    private String content;          // 리뷰 내용
    private String reviewImageUrl;   // 리뷰 이미지 URL
    private LocalDateTime createdAt; // 작성 시간
}
