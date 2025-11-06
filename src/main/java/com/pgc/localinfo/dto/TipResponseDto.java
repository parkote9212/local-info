package com.pgc.localinfo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pgc.localinfo.domain.LibraryTip;
import lombok.AllArgsConstructor; // [추가]
import lombok.Builder;          // [추가]
import lombok.Getter;
import lombok.NoArgsConstructor; // [추가]

import java.time.LocalDateTime;

@Getter
@Builder                 // [추가] 빌더 패턴 활성화
@NoArgsConstructor       // [추가] 기본 생성자
@AllArgsConstructor      // [추가] @Builder가 사용할 모든 필드 생성자
public class TipResponseDto {

    private Long tipId;
    private String content;
    private String authorNickname; // 작성자 닉네임
    private LocalDateTime createdAt;

    // [수정] private boolean isOwner; -> private boolean owner;
    private boolean owner;

    // 엔티티를 DTO로 변환 (정적 팩토리 메서드)
    // '로그인한 사용자'가 이 팁의 '소유자'인지 확인하는 로직 포함
    public static TipResponseDto fromEntity(LibraryTip entity, String currentUsername) {

        // [수정] 비로그인 사용자(currentUsername == null)를 안전하게 처리
        boolean isOwnerCheck = (currentUsername != null) &&
                (entity.getMember().getUsername().equals(currentUsername));

        // [수정] new Dto() + setter 대신 Builder 패턴 사용
        return TipResponseDto.builder()
                .tipId(entity.getId())
                .content(entity.getContent())
                .authorNickname(entity.getMember().getNickname())
                .createdAt(entity.getCreatedAt())
                .owner(isOwnerCheck)
                .build();
    }
}