package com.example.Python_Back.Domain.ByulBook.Entity;

import com.example.Python_Back.Domain.KaKao.Entity.KakaoUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Entity
@Getter
@Setter
public class Shelf {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ShelfId;

    @OneToOne
    @JoinColumn(name = "kakaoId")
    private KakaoUser kakaoUser;  // 사용자와의 1:1 관계

    // @OneToMany(mappedBy = "bookshelf", cascade = CascadeType.ALL)
    // private List<BookshelfBook> books = new ArrayList<>();



}
