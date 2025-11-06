package com.pgc.localinfo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * '팁 수정' (AJAX PUT) 요청 시 사용할 DTO
 */
@Getter
@Setter
public class TipUpdateRequestDto {

    @NotBlank(message = "내용을 입력해주세요.")
    @Size(min = 5, max = 500, message = "팁은 5자 이상 500자 이하로 작성해주세요.")
    private String content;
}