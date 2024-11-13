package com.example.Python_Back.Domain.ByulBook.Entity;

import com.example.Python_Back.Domain.KaKao.Entity.KakaoUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewID;

    @ManyToOne
    @JoinColumn(name = "bookId")
    private Book book;

    @ManyToOne
    @JoinColumn(name = "kakaoId")
    private KakaoUser kakaoUser;

    private String content;

    private LocalDateTime createdAt;
}
