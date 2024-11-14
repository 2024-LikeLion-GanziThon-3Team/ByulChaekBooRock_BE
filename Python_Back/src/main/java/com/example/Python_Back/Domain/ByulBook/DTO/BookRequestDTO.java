package com.example.Python_Back.Domain.ByulBook.DTO;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BookRequestDTO {
    private List<String> titles;  // 책 제목 리스트
}
