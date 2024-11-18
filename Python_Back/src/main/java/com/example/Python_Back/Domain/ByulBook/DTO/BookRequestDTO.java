package com.example.Python_Back.Domain.ByulBook.DTO;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BookRequestDTO {

    private List<BookInfo> books; // 제목과 표지 정보를 포함한 리스트

    // Getter, Setter

    @Getter
    @Setter
    public static class BookInfo {
        private String title; // 책 제목
        private String coverImageUrl; // 책 표지 URL
    }
}

