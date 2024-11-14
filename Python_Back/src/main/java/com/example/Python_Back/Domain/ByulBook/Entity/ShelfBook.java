package com.example.Python_Back.Domain.ByulBook.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Entity
@Getter
@Setter

public class ShelfBook {
    public enum BookStatus {
        다읽은책,
        덜읽은책,
        안읽은책,
        또읽을책
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long shelfBookId;  // 책장에 추가된 책의 고유 ID

    @ManyToOne
    @JoinColumn(name = "shelfId")
    private Shelf shelf;  // 서재와의 다대일 관계

    @ManyToOne
    @JoinColumn(name = "bookId")
    private Book book;  // 책과의 다대일 관계

    @Enumerated(EnumType.STRING)
    private BookStatus status;  // 책 상태 (다 읽은 책, 덜 읽은 책, 안 읽은 책)

   // @OneToMany(mappedBy = "shelfBook", cascade = CascadeType.ALL)
    //private List<Bookmark> bookmarks = new ArrayList<>();  // 책갈피 목록


}
