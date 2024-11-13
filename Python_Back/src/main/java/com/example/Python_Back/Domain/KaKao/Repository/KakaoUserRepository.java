package com.example.Python_Back.Domain.KaKao.Repository;

import com.example.Python_Back.Domain.KaKao.Entity.KakaoUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KakaoUserRepository extends JpaRepository<KakaoUser, Long> {
    Optional<KakaoUser> findByKakaoId(Long kakaoId);
    void deleteByKakaoId(Long kakaoId);
}
