package com.example.Python_Back.Domain.KaKao.Entity;

import com.example.Python_Back.Domain.ByulBook.Entity.Shelf;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class KakaoUser {

    @Id
    @Column(nullable = false, unique = true)
    private Long kakaoId;

    // 닉네임
    @Column(nullable = false)
    private String nickname;

    // 연결된 시간 (카카오 로그인)
    private LocalDateTime connectedAt;

    @OneToOne(mappedBy = "kakaoUser", cascade = CascadeType.ALL)
    private Shelf shelf;

    // 기본 생성자
    public KakaoUser() {
        // 사용자가 생성될 때 서재를 자동으로 생성
        this.shelf = new Shelf(this);  // Shelf 생성 시 현재 KakaoUser와 연결
    }

    // 모든 필드를 받는 생성자
    public KakaoUser(Long kakaoId, String nickname, LocalDateTime connectedAt) {
        this.kakaoId = kakaoId;
        this.nickname = nickname;
        this.connectedAt = connectedAt;

        // 사용자가 생성될 때 서재를 자동으로 생성
        this.shelf = new Shelf(this);
    }
}
