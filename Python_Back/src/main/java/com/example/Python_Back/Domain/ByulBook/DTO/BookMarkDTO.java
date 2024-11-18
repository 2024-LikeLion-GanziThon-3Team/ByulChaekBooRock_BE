package com.example.Python_Back.Domain.ByulBook.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class BookMarkDTO {
    private Long bookmarkId;
    private Long shelfBookId; // ShelfBook의 ID 추가
    private Integer pageNumber;
    private String content;
    private LocalDateTime createdAt;

}