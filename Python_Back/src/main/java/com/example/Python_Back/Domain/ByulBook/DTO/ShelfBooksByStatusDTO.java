package com.example.Python_Back.Domain.ByulBook.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ShelfBooksByStatusDTO {
    private Long kakaoId; // 사용자 식별자
    private List<ShelfBookResponseDTO> readBooks; // 다 읽은 책
    private List<ShelfBookResponseDTO> partiallyReadBooks; // 덜 읽은 책
    private List<ShelfBookResponseDTO> unreadBooks; // 안 읽은 책
    private List<ShelfBookResponseDTO>  rereadBooks; // 또 읽을 책
}
