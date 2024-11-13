package com.example.Python_Back.Domain.ByulBook.DTO;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BookResponseDTO {
    private String title;  // 책 제목
    private List<String> reviews;  // 감상 리스트
}
