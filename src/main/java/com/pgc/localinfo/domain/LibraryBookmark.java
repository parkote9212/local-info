package com.pgc.localinfo.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "Library_bookmark")
public class LibraryBookmark extends BaseTimeEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bookmark_id")
    private Long id;

    //FetchType.LAZY: 북마크 조회시 회원 정보는 필요할 때만 로드
    @ManyToOne(fetch = FetchType.LAZY)
    //FK member_id
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    //도서관정보

    @Column(nullable = false)
    private String libraryName;

    @Column(nullable = false)
    private String sigunName;

    // 주소를 유니크 키로 사용하여 중복 찜을 방지할 수 있습니다.
    @Column(nullable = false)
    private String address;

    private String latitude;
    private String longitude;
    private String homepageUrl;

    @Builder
    public LibraryBookmark(Member member, String libraryName, String sigunName, String address, String latitude, String longitude, String homepageUrl) {
        this.member = member;
        this.libraryName = libraryName;
        this.sigunName = sigunName;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.homepageUrl = homepageUrl;
    }
}
