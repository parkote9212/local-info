package com.pgc.localinfo.dto;

import com.pgc.localinfo.domain.Member;
import com.pgc.localinfo.domain.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberSignupRequestDto {

    @NotBlank(message = "아이디는 필수 입력 값입니다.")
    @Size(min = 4, max = 20, message = "아이디는 4자 이상 20자 이하로 입력해주세요.")
    private String username;

    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하로 입력해주세요.")
    private String password;

    @NotBlank(message = "닉네임은 필수 입력 값입니다.")
    @Size(min = 2, max = 10, message = "닉네임은 2자 이상 10자 이하로 입력해주세요.")
    private String nickname;

    // DTO를 엔티티로 변환하는 메서드 (Service에서 사용)
    // 비밀번호 암호화는 Service 레이어에서 처리할 예정
    public Member toEntity(String encodedPassword) {
        return Member.builder()
                .username(this.username)
                .password(encodedPassword)
                .nickname(this.nickname)
                .role(Role.USER)
                .build();
    }
}