package com.pgc.localinfo.service;

import com.pgc.localinfo.domain.LibraryBookmark;
import com.pgc.localinfo.domain.Member;
import com.pgc.localinfo.dto.MemberSignupRequestDto;
import com.pgc.localinfo.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class MemberServiceTest {

    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    PasswordEncoder passwordEncoder;


    //테스트용 DTO 생성
    private MemberSignupRequestDto createSignupDto(String username, String nickname, String password) {
        MemberSignupRequestDto dto = new MemberSignupRequestDto();
        dto.setUsername(username);
        dto.setNickname(nickname);
        dto.setPassword(password);
        return dto;
    }

    // TDD1
    @Test
    @DisplayName("회원가입성공")
    void signup_Success(){
        // Given
        MemberSignupRequestDto requestDto = createSignupDto("newUser", "새유저", "password123!");
        // When
        Long savedMemberId = memberService.signup(requestDto);
        // Then
        assertThat(savedMemberId).isNotNull();

        Member foundMember = memberRepository.findById(savedMemberId)
                .orElseThrow(() -> new AssertionError("저장된 멤버를 찾을 수 없습니다."));

        assertThat(foundMember.getUsername()).isEqualTo("newUser");

        assertThat(foundMember.getPassword()).isNotEqualTo("password123!");
        assertThat(passwordEncoder.matches("password123!", foundMember.getPassword())).isTrue();
    }

    @Test
    @DisplayName("회원가입 실패 - 이름중복")
    void signup_Fail_DuplicateUsername(){
        //Given
        MemberSignupRequestDto firstDto = createSignupDto("existUser", "기존유저", "pass1");
        memberService.signup(firstDto); // (이 테스트는 signup_Success()가 통과해야 함께 통과됨)

        // 2. 동일한 username으로 가입 시도
        MemberSignupRequestDto duplicateDto = createSignupDto("existUser", "다른유저", "pass2");

        // When & Then
        assertThatThrownBy(() -> memberService.signup(duplicateDto))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("이미 존재하는 아이디입니다.");
    }

    @Test
    @DisplayName("회원가입 실패 - 중복된 nickname")
    void signup_Fail_DuplicateNickname() {
        // Given
        MemberSignupRequestDto firstDto = createSignupDto("user1", "existNickname", "pass1");
        memberService.signup(firstDto);

        MemberSignupRequestDto duplicateDto = createSignupDto("user2", "existNickname", "pass2");

        // When & Then
        assertThatThrownBy(() -> memberService.signup(duplicateDto))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("이미 존재하는 닉네임입니다.");
    }
}