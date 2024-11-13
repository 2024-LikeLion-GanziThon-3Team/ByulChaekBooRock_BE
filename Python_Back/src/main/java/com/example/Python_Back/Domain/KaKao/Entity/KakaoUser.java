package com.example.Python_Back.Domain.KaKao.Entity;

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

    // 프로필 이미지 URL (옵션)
   // private String profileImageUrl;

    // 연결된 시간 (카카오 로그인)
    private LocalDateTime connectedAt;

    // 액세스 토큰 (로그인 시 저장)
    // private String accessToken;

    // 리프레시 토큰 (필요 시 갱신용으로 저장)
   // private String refreshToken;

    // 기본 생성자
    public KakaoUser() {}

    // 모든 필드를 받는 생성자
    public KakaoUser(Long kakaoId, String nickname, LocalDateTime connectedAt) {
        this.kakaoId = kakaoId;
        this.nickname = nickname;
        //this.profileImageUrl = profileImageUrl;
        this.connectedAt = connectedAt;
       // this.accessToken = accessToken;

    }


}
