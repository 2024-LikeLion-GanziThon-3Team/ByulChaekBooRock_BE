package com.example.Python_Back.Domain.ByulBook.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class BookMarkResponseDTO {
    private Long bookmarkId;              // 책갈피 ID
    private Integer pageNumber;           // 페이지 번호
    private String content;               // 책갈피 내용
    private LocalDateTime createdAt;      // 생성일


}
