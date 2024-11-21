package com.example.Python_Back.Domain.ByulBook.Entity;

import com.example.Python_Back.Domain.KaKao.Entity.KakaoUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class BookMark {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookmarkId;

    @ManyToOne
    @JoinColumn(name = "kakaoId", nullable = false) // 사용자와 연관
    private KakaoUser kakaoUser;

    @ManyToOne
    @JoinColumn(name = " ShelfBookId")
    private ShelfBook shelfBook;

    private Integer pageNumber;

    @Column(length = 1000)
    private String content;

    private LocalDateTime createdAt;

}
