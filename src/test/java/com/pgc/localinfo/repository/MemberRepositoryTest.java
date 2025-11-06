package com.pgc.localinfo.repository;

import com.pgc.localinfo.domain.Member;
import com.pgc.localinfo.domain.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("회원 저장 및 조회")
    void saveAndFindMemberTest() {

        //Given
        Member newMember = Member.builder()
                .username("testuser")
                .password("testpass123")
                .nickname("테스터")
                .role(Role.USER)
                .build();

        //When
        memberRepository.save(newMember);

        Member foundMember = memberRepository.findByUsername("testuser")
                .orElse(null);

        //Then
        assertThat(foundMember).isNotNull();

        assertThat(foundMember.getId()).isNotNull();

        assertThat(foundMember.getCreatedAt()).isNotNull();
        assertThat(foundMember.getUpdatedAt()).isNotNull();

        assertThat(foundMember.getUsername()).isEqualTo(newMember.getUsername());
        assertThat(foundMember.getNickname()).isEqualTo(newMember.getNickname());
        assertThat(foundMember.getRole()).isEqualTo(Role.USER);
    }
}
