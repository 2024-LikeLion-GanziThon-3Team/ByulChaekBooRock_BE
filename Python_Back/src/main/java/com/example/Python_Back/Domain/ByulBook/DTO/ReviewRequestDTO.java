package com.example.Python_Back.Domain.ByulBook.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewRequestDTO {
    private Long bookId;
    private String content;
    private String reviewImageUrl; // 선택적
}
