package com.pgc.localinfo.service;

import com.pgc.localinfo.domain.LibraryTip;
import com.pgc.localinfo.domain.Member;
import com.pgc.localinfo.domain.Role;
import com.pgc.localinfo.dto.TipCreateRequestDto;
import com.pgc.localinfo.dto.TipResponseDto;
import com.pgc.localinfo.repository.LibraryTipRepository;
import com.pgc.localinfo.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class TipServiceTest {

    @Autowired TipService tipService;
    @Autowired MemberRepository memberRepository;
    @Autowired LibraryTipRepository tipRepository;

    private Member testMember;
    private Member otherMember;
    private LibraryTip myTip;
    private final String libraryAddress = "도서관 주소 1";

    @BeforeEach
    void setUp() {
        // 1. 테스트 회원 2명 생성
        testMember = Member.builder().username("testUser").password("p1").nickname("테스터").role(Role.USER).build();
        otherMember = Member.builder().username("otherUser").password("p2").nickname("타인").role(Role.USER).build();
        memberRepository.saveAll(List.of(testMember, otherMember));

        // 2. "도서관 주소 1"에 팁 2개 생성 (1개는 testUser, 1개는 otherUser)
        this.myTip = LibraryTip.builder()
                .member(testMember)
                .libraryAddress(libraryAddress)
                .libraryName("A 도서관")
                .content("테스터가 쓴 팁")
                .build();

        LibraryTip otherTip = LibraryTip.builder() // [수정] otherTip은 지역 변수
                .member(otherMember)
                .libraryAddress(libraryAddress)
                .libraryName("A 도서관")
                .content("타인이 쓴 팁")
                .build();
        tipRepository.saveAll(List.of(this.myTip, otherTip));
    }

    // --- 팁 생성(Create) 테스트 ---
    @Test
    @DisplayName("팁 작성 성공 (C)")
    void createTip_Success() {
        // [Given]
        String username = "testUser";
        TipCreateRequestDto requestDto = new TipCreateRequestDto();
        requestDto.setLibraryAddress("새로운 도서관 주소 2");
        requestDto.setLibraryName("B 도서관");
        requestDto.setContent("새로운 팁 내용 (5자 이상)");

        // [When]
        Long savedTipId = tipService.createTip(username, requestDto);

        // [Then]
        assertThat(savedTipId).isNotNull();
        LibraryTip foundTip = tipRepository.findById(savedTipId).orElseThrow();
        assertThat(foundTip.getContent()).isEqualTo("새로운 팁 내용 (5자 이상)");
        assertThat(foundTip.getMember().getUsername()).isEqualTo("testUser");
    }

    // --- 팁 조회(Read) 테스트 ---
    @Test
    @DisplayName("팁 목록 조회 성공 (R) - 로그인 사용자 기준")
    void getTipsByLibraryAddress_AsOwner() {
        // [Given]
        String currentUsername = "testUser"; // "테스터" (본인)

        // [When]
        // "도서관 주소 1"의 팁 목록을 "testUser"가 조회
        List<TipResponseDto> tips = tipService.getTipsByLibraryAddress(libraryAddress, currentUsername);

        // [Then]
        assertThat(tips).hasSize(2); // 2개 조회 (최신순 정렬)

        TipResponseDto otherTip = tips.get(0); // tip2 (타인)
        TipResponseDto myTip = tips.get(1);    // tip1 (본인)

        // "타인이 쓴 팁" (otherUser)
        assertThat(otherTip.getAuthorNickname()).isEqualTo("타인");
        assertThat(otherTip.isOwner()).isFalse(); // [중요] 본인 팁이 아님

        // "테스터가 쓴 팁" (testUser)
        assertThat(myTip.getAuthorNickname()).isEqualTo("테스터");
        assertThat(myTip.isOwner()).isTrue(); // [중요] 본인 팁임
    }

    @Test
    @DisplayName("팁 목록 조회 성공 (R) - 비로그인 사용자 기준")
    void getTipsByLibraryAddress_AsAnonymous() {
        // [Given]
        String currentUsername = null; // 비로그인 상태

        // [When]
        List<TipResponseDto> tips = tipService.getTipsByLibraryAddress(libraryAddress, currentUsername);

        // [Then]
        assertThat(tips).hasSize(2);
        // 비로그인 사용자에게는 모든 팁이 '본인 팁 아님'으로 보여야 함
        assertThat(tips.get(0).isOwner()).isFalse();
        assertThat(tips.get(1).isOwner()).isFalse();
    }

    @Test
    @DisplayName("팁 수정 성공 (U) - 본인 팁")
    void updateTip_Success() {
        // [Given]
        Long tipId = this.myTip.getId(); // "테스터가 쓴 팁"의 ID
        String newContent = "내용 수정 테스트 (5자 이상)";
        String username = "testUser"; // 본인

        // [When]
        tipService.updateTip(tipId, newContent, username);

        // [Then]
        // @Transactional 덕분에 DB에서 다시 조회(find)하면
        // Dirty Checking에 의해 update 쿼리가 실행된 후의 엔티티가 조회됨
        LibraryTip updatedTip = tipRepository.findById(tipId).orElseThrow();
        assertThat(updatedTip.getContent()).isEqualTo(newContent);
    }

    @Test
    @DisplayName("팁 수정 실패 (U) - 타인 팁 (권한 없음)")
    void updateTip_Fail_NotOwner() {
        // [Given]
        Long tipId = this.myTip.getId(); // "테스터가 쓴 팁"의 ID
        String newContent = "해커가 수정한 내용";
        String attackerUsername = "otherUser"; // 타인 (otherMember)

        // [When] & [Then]
        // "otherUser"가 "testUser"의 팁 수정을 시도
        assertThatThrownBy(() -> tipService.updateTip(tipId, newContent, attackerUsername))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("수정 권한이 없거나 존재하지 않는 팁입니다.");
    }

    // --- 팁 삭제(Delete) 테스트 ---

    @Test
    @DisplayName("팁 삭제 성공 (D) - 본인 팁")
    void deleteTip_Success() {
        // [Given]
        Long tipId = this.myTip.getId(); // "테스터가 쓴 팁"의 ID
        String username = "testUser"; // 본인

        // [When]
        tipService.deleteTip(tipId, username);

        // [Then]
        assertThat(tipRepository.findById(tipId).isPresent()).isFalse();
    }

    @Test
    @DisplayName("팁 삭제 실패 (D) - 타인 팁 (권한 없음)")
    void deleteTip_Fail_NotOwner() {
        // [Given]
        Long tipId = this.myTip.getId(); // "테스터가 쓴 팁"의 ID
        String attackerUsername = "otherUser"; // 타인

        // [When] & [Then]
        assertThatThrownBy(() -> tipService.deleteTip(tipId, attackerUsername))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("삭제 권한이 없거나 존재하지 않는 팁입니다.");
    }
}