package com.example.Python_Back.Domain.ByulBook.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ShelfBookResponseDTO {
    private Long shelfBookId;
    private String title;
    private String coverImageUrl;
    private List<BookMarkResponseDTO> bookmarks; // 책갈피 리스트
}
