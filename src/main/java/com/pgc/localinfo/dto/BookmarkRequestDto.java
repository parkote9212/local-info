package com.pgc.localinfo.dto;


import com.pgc.localinfo.domain.LibraryBookmark;
import com.pgc.localinfo.domain.Member;
import lombok.Getter;
import lombok.Setter;

/**
 * '찜하기' (AJAX POST) 요청 시 뷰(JavaScript)에서 컨트롤러로
 * 도서관 정보를 전달할 때 사용하는 DTO
 */
@Getter
@Setter
public class BookmarkRequestDto {

    private String libraryName;
    private String sigunName;
    private String address;
    private String latitude;
    private String longitude;
    private String homepageUrl;

    public LibraryBookmark toEntity(Member member) {
        return LibraryBookmark.builder()
                .member(member)
                .libraryName(this.libraryName)
                .sigunName(this.sigunName)
                .address(this.address)
                .latitude(this.latitude)
                .longitude(this.longitude)
                .homepageUrl(this.homepageUrl)
                .build();

    }
}
