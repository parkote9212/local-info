package com.pgc.localinfo.dto;

import com.pgc.localinfo.domain.LibraryTip;
import com.pgc.localinfo.domain.Member;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TipCreateRequestDto {

    // 1. 팁이 달릴 도서관의 고유 식별자 (주소)
    @NotBlank
    private String libraryAddress;

    // 2. (선택) 팁 목록에 도서관 이름을 함께 보여주기 위함
    @NotBlank
    private String libraryName;

    // 3. 팁 내용 (유효성 검사)
    @NotBlank(message = "내용을 입력해주세요.")
    @Size(min = 5, max = 500, message = "팁은 5자 이상 500자 이하로 작성해주세요.")
    private String content;

    // 이 DTO를 Member 정보와 결합하여 엔티티로 변환하는 메서드
    public LibraryTip toEntity(Member member) {
        return LibraryTip.builder()
                .member(member)
                .libraryAddress(this.libraryAddress)
                .libraryName(this.libraryName)
                .content(this.content)
                .build();
    }
}