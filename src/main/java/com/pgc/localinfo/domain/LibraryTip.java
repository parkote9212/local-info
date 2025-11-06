package com.pgc.localinfo.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "library_tip")
public class LibraryTip extends BaseTimeEntity { // createdAt, updatedAt 상속

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tip_id")
    private Long id;

    // 1. (N:1) 이 팁을 작성한 회원
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false) // FK
    private Member member;

    // 2. (핵심) 이 팁이 달린 도서관의 '주소' (API의 고유 식별자 역할)
    // 이 주소를 기준으로 특정 도서관의 팁 목록을 조회합니다.
    @Column(nullable = false)
    private String libraryAddress;

    // 3. (선택) 팁 목록에 도서관 이름을 함께 보여주기 위해 복사 저장
    @Column(nullable = false)
    private String libraryName;

    // 4. 팁 내용
    @Column(nullable = false, length = 500) // 500자 제한
    private String content;

    @Builder
    public LibraryTip(Member member, String libraryAddress, String libraryName, String content) {
        this.member = member;
        this.libraryAddress = libraryAddress;
        this.libraryName = libraryName;
        this.content = content;
    }

    // (선택) 팁 내용 수정 메서드
    public void updateContent(String content) {
        this.content = content;
    }
}