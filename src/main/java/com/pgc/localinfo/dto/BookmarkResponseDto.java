package com.pgc.localinfo.dto;

import com.pgc.localinfo.domain.LibraryBookmark;
import lombok.AllArgsConstructor; // [추가]
import lombok.Builder;          // [추가]
import lombok.Getter;
import lombok.NoArgsConstructor; // [추가]
import lombok.Setter;

/**
 * '마이페이지'에서 찜 목록을 응답할 때 사용할 DTO
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookmarkResponseDto {

    private Long bookmarkId;
    private String sigunName;
    private String libraryName;
    private String address;
    private String homepageUrl;
    private String latitude;
    private String longitude;


    public static BookmarkResponseDto fromEntity(LibraryBookmark entity) {

        return BookmarkResponseDto.builder()
                .bookmarkId(entity.getId())
                .sigunName(entity.getSigunName())
                .libraryName(entity.getLibraryName())
                .address(entity.getAddress())
                 .homepageUrl(entity.getHomepageUrl())
                .latitude(entity.getLatitude())
                .longitude(entity.getLongitude())
                .build();
    }
}