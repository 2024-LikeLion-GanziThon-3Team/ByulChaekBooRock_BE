package com.example.Python_Back.Domain.ByulBook.Repository;

import com.example.Python_Back.Domain.ByulBook.Entity.ShelfBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShelfBookRepository extends JpaRepository<ShelfBook, Long> {
    // 특정 사용자의 서재에서 특정 책을 찾는 메서드
    Optional<ShelfBook> findByShelf_ShelfIdAndBook_BookId(Long shelfId, Long bookId);

    // 특정 서재의 모든 책 반환
    List<ShelfBook> findByShelf_ShelfId(Long shelfId);

    // 특정 상태의 책 목록 반환
    List<ShelfBook> findByShelf_ShelfIdAndStatus(Long shelfId, ShelfBook.BookStatus status);
}
