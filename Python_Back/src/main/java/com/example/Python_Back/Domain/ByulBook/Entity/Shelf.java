package com.example.Python_Back.Domain.ByulBook.Entity;

import com.example.Python_Back.Domain.KaKao.Entity.KakaoUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Shelf {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long shelfId;  // 서재 고유 ID

    @OneToOne
    @JoinColumn(name = "kakaoId")
    private KakaoUser kakaoUser;  // 사용자와의 1:1 관계

    @OneToMany(mappedBy = "shelf", cascade = CascadeType.ALL)
    private List<ShelfBook> shelfBooks = new ArrayList<>();  // 서재에 있는 책 목록


    // 기본 생성자
    public Shelf() {}

    // KakaoUser를 받는 생성자
    public Shelf(KakaoUser kakaoUser) {
        this.kakaoUser = kakaoUser;
    }



}
