package com.example.Python_Back.Domain.KaKao.Service;

import com.example.Python_Back.Domain.ByulBook.Entity.Shelf;
import com.example.Python_Back.Domain.KaKao.Entity.KakaoUser;
import com.example.Python_Back.Domain.KaKao.Repository.KakaoUserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

@Service
@Slf4j
public class AuthService {
    @Value("${kakao.api.key}")
    private String restApiKey;

    @Value("${kakao.redirect_uri}")
    private String redirectUri;


    private final KakaoUserRepository kakaoUserRepository;

    // 생성자 주입을 사용하여 의존성 주입
    public AuthService(KakaoUserRepository kakaoUserRepository) {
        this.kakaoUserRepository = kakaoUserRepository;
    }


    RestTemplate restTemplate = new RestTemplate();
    public String kakaoGetAccessViaCode(String code) {

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("code", code);
        body.add("client_id", restApiKey); // 카카오 REST API 키
        body.add("redirect_uri", redirectUri);
        body.add("grant_type", "authorization_code");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<JsonNode> responseNode = restTemplate.exchange("https://kauth.kakao.com/oauth/token", HttpMethod.POST, entity, JsonNode.class);
        String accessToken = responseNode.getBody().get("access_token").asText();
        return accessToken;
    }
    // 카카오 사용자 정보 가져오기 및 DB에 저장
    @Transactional
    public KakaoUser kakaoGetUserInfoViaAccessToken(String accessToken) {
        try {
            // 1. 카카오 API로 사용자 정보 가져오기
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken);

            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<JsonNode> responseNode = restTemplate.exchange(
                    "https://kapi.kakao.com/v2/user/me", HttpMethod.GET, entity, JsonNode.class
            );
            JsonNode userInfo = responseNode.getBody();

            // 2. 사용자 정보 추출
            Long kakaoId = userInfo.get("id").asLong();
            String nickname = userInfo.get("properties").get("nickname").asText();
            String profileImageUrl = userInfo.get("properties").get("profile_image").asText();
            String connectedAtString = userInfo.get("connected_at").asText();
            LocalDateTime connectedAt = LocalDateTime.parse(connectedAtString.substring(0, 19));

            // 3. 기존 사용자 확인 또는 생성
            KakaoUser kakaoUser = kakaoUserRepository.findById(kakaoId).orElseGet(() -> {
                // 새로운 사용자 생성
                KakaoUser newUser = new KakaoUser();
                newUser.setKakaoId(kakaoId);
                newUser.setNickname(nickname);
                newUser.setConnectedAt(connectedAt);
                return newUser;
            });

            // 4. 서재 확인 및 생성
            if (kakaoUser.getShelf() == null) {
                Shelf shelf = new Shelf();
                shelf.setKakaoUser(kakaoUser);
                kakaoUser.setShelf(shelf);
            }

            // 5. 사용자 정보 저장
            kakaoUserRepository.save(kakaoUser);

            return kakaoUser;
        } catch (Exception e) {
            log.error("카카오 사용자 정보 조회 실패", e);
            throw new RuntimeException("카카오 사용자 정보 조회에 실패했습니다.", e);
        }
    }

    @Transactional
    public String kakaoLogout(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "Bearer " + token);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<JsonNode> responseNode = restTemplate.exchange(
                "https://kapi.kakao.com/v1/user/logout",
                HttpMethod.POST,
                entity,
                JsonNode.class
        );

        log.info("로그아웃 응답: {}", responseNode.getBody().toPrettyString());

        return responseNode.getBody().toString();
    }

    @Transactional
    public String kakaoUnlink(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "Bearer " + token);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<JsonNode> responseNode = restTemplate.exchange(
                "https://kapi.kakao.com/v1/user/unlink",
                HttpMethod.POST,
                entity,
                JsonNode.class
        );
        // 응답에서 사용자 ID 추출
        Long kakaoId = responseNode.getBody().get("id").asLong();

        // 데이터베이스에서 사용자 삭제
        kakaoUserRepository.deleteByKakaoId(kakaoId);

        log.info("탈퇴 응답: {}", responseNode.getBody().toPrettyString());

        // 반환할 때 메시지를 포함한 전체 JSON을 반환하도록 조정
        return responseNode.getBody().toString(); // 필요에 따라 반환 형식을 조정
    }

    @Transactional
    public Long kakaoGetUserIdFromTokenInfo(String accessToken) {
        String url = "https://kapi.kakao.com/v1/user/access_token_info";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        log.info("Authorization 헤더 설정 완료: Bearer " + accessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<JsonNode> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, JsonNode.class);

            JsonNode body = response.getBody();
            log.info("카카오 API 응답 본문: " + body);

            if (body != null && body.has("id")) {
                Long kakaoUserId = body.get("id").asLong();
                log.info("카카오 사용자 ID: " + kakaoUserId);
                return kakaoUserId;
            } else {
                log.error("토큰 정보 조회 실패 또는 유효하지 않은 토큰입니다. 응답 본문: " + body);
                return null;
            }

        } catch (HttpClientErrorException e) {
            log.error("카카오 API 클라이언트 오류: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
            throw new RuntimeException("카카오 API 클라이언트 오류", e);
        } catch (Exception e) {
            log.error("카카오 토큰 정보 조회 실패", e);
            throw new RuntimeException("카카오 토큰 정보 조회에 실패했습니다.", e);
        }
    }



}
