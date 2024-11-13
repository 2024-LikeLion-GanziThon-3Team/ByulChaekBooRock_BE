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
        안읽은책
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ShelfBookId;

    @ManyToOne
    @JoinColumn(name = "ShelfId")
    private Shelf shelf;

    @Enumerated(EnumType.STRING)
    private BookStatus status;

   // @OneToMany(mappedBy = "shelfBook", cascade = CascadeType.ALL)
    //private List<Bookmark> bookmarks = new ArrayList<>();  // 책갈피 목록


}
