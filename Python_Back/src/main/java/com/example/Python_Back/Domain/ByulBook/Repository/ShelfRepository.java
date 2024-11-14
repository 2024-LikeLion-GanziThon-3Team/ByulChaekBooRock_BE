package com.example.Python_Back.Domain.ByulBook.Repository;

import com.example.Python_Back.Domain.ByulBook.Entity.Shelf;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShelfRepository extends JpaRepository<Shelf, Long> {
    Optional<Shelf> findByKakaoUser_KakaoId(Long kakaoId);
}
