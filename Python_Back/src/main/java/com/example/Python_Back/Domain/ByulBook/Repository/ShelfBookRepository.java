package com.example.Python_Back.Domain.ByulBook.Repository;

import com.example.Python_Back.Domain.ByulBook.Entity.ShelfBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShelfBookRepository extends JpaRepository<ShelfBook, Long> {
    // 필요 시 추가적인 쿼리 메서드 작성 가능
}
