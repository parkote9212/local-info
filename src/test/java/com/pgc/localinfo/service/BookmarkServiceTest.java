package com.pgc.localinfo.service;

import com.pgc.localinfo.domain.LibraryBookmark;
import com.pgc.localinfo.domain.Member;
import com.pgc.localinfo.domain.Role;
import com.pgc.localinfo.dto.BookmarkRequestDto;
import com.pgc.localinfo.dto.BookmarkResponseDto;
import com.pgc.localinfo.repository.LibraryBookmarkRepository;
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
class BookmarkServiceTest {

    @Autowired
    BookmarkService bookmarkService;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    LibraryBookmarkRepository bookmarkRepository;

    private Member testMember;
    private LibraryBookmark bookmarkA;


    @BeforeEach
    void setUp() {
        // 1. 테스트용 회원 1명 저장
        this.testMember = Member.builder()
                .username("myPageUser")
                .password("testpass")
                .nickname("마이페이지유저")
                .role(Role.USER)
                .build();
        memberRepository.save(this.testMember);

        // 2. 이 회원이 찜한 도서관 2개 저장
        this.bookmarkA = LibraryBookmark.builder()
                .member(this.testMember)
                .libraryName("A 도서관")
                .sigunName("용인시")
                .address("주소 1")
                .homepageUrl("http://a.com")
                .latitude("37.1")
                .longitude("127.1")
                .build();

        LibraryBookmark bookmarkB = LibraryBookmark.builder()
                .member(this.testMember)
                .libraryName("B 도서관")
                .sigunName("수원시")
                .address("주소 2")
                .homepageUrl("http://b.com")
                .latitude("37.2")
                .longitude("127.2")
                .build();

        // 이 라인(saveAll)에서 오류가 발생했습니다.
        bookmarkRepository.saveAll(List.of(this.bookmarkA, bookmarkB));
    }

    @Test
    @DisplayName("내 찜 목록 조회")
    void getMyBookmarks_Success() {
        // [Given]
        String username = "myPageUser";

        // [When]
        List<BookmarkResponseDto> myBookmarks = bookmarkService.getMyBookmarks(username);

        // [Then]
        // 1. 찜 목록이 null이 아니어야 함
        assertThat(myBookmarks).isNotNull().hasSize(2);
        assertThat(myBookmarks.get(0).getLibraryName()).isEqualTo("B 도서관");
        // 4. [추가] 1번째(이전)는 "A 도서관"이어야 함
        assertThat(myBookmarks.get(1).getLibraryName()).isEqualTo("A 도서관");
    }

    @Test
    @DisplayName("찜 목록이 없는 사용자 조회")
    void getMyBookmarks_Empty() {
        // [Given]
        // 1. 찜 목록이 없는 새 회원 저장
        Member noBookmarkMember = Member.builder()
                .username("emptyUser")
                .password("pass")
                .nickname("empty")
                .role(Role.USER)
                .build();
        memberRepository.save(noBookmarkMember);

        // [When]
        List<BookmarkResponseDto> myBookmarks = bookmarkService.getMyBookmarks("emptyUser");

        // [Then]
        assertThat(myBookmarks).isNotNull().isEmpty(); // 2. null이 아니어야 함
//        assertThat(myBookmarks).isEmpty(); // 3. 리스트가 비어있어야 함
    }

    // [추가] 찜하기(Create) 테스트
    @Test
    @DisplayName("새로운 찜하기 성공")
    void addBookmark_Success() {
        // [Given]
        String username = "myPageUser"; // setUp에서 생성한 회원

        // 찜하려는 도서관 정보 (C 도서관)
        BookmarkRequestDto requestDto = new BookmarkRequestDto();
        requestDto.setLibraryName("C 도서관");
        requestDto.setAddress("새로운 주소 3"); // 중복되지 않는 주소
        requestDto.setSigunName("성남시");
        requestDto.setHomepageUrl("http://c.com");
        requestDto.setLatitude("37.3");
        requestDto.setLongitude("127.3");

        // [When]
        Long savedBookmarkId = bookmarkService.addBookmark(username, requestDto);

        // [Then]
        assertThat(savedBookmarkId).isNotNull(); // 1. ID가 반환되었는지

        // 2. DB에 실제로 저장되었는지 확인
        LibraryBookmark foundBookmark = bookmarkRepository.findById(savedBookmarkId)
                .orElseThrow(() -> new AssertionError("저장된 북마크를 찾을 수 없습니다."));

        assertThat(foundBookmark.getLibraryName()).isEqualTo("C 도서관");
        assertThat(foundBookmark.getMember().getUsername()).isEqualTo(username);
    }

    @Test
    @DisplayName("찜하기 실패 - 이미 찜한 도서관 (주소 중복)")
    void addBookmark_Fail_DuplicateAddress() {
        // [Given]
        String username = "myPageUser"; // setUp에서 생성한 회원
        // setUp에서 "A 도서관"을 "주소 1"로 이미 저장했음

        BookmarkRequestDto duplicateDto = new BookmarkRequestDto();
        duplicateDto.setLibraryName("A 도서관 (이름은 달라도 됨)");
        duplicateDto.setAddress("주소 1"); // [중복!]
        duplicateDto.setSigunName("용인시");

        // [When] & [Then]
        // "주소 1"을 다시 찜하려고 시도하면 예외가 발생해야 함
        assertThatThrownBy(() -> bookmarkService.addBookmark(username, duplicateDto))
                .isInstanceOf(IllegalStateException.class) // IllegalStateException 예외
                .hasMessageContaining("이미 찜한 도서관입니다."); // 이 메시지 포함
    }

    @Test
    @DisplayName("찜 삭제 성공(본인)")
    void deleteBookmark_Success() {
        //given
        String username = "myPageUser";
        Long bookmarkIdToDelete = this.bookmarkA.getId();

        //when
        bookmarkService.deleteBookmark(bookmarkIdToDelete, username);

        //then
        boolean isPresent = bookmarkRepository.findById(bookmarkIdToDelete).isPresent();
        assertThat(isPresent).isFalse();
    }

    @Test
    @DisplayName("찜 삭제 실패 - 타인의 찜 (권한 없음)")
    void deleteBookmark_Fail_NotOwner() {
        // [Given]
        // 1. 다른 사용자(악의적 사용자) 생성
        Member attacker = Member.builder()
                .username("attacker")
                .password("pass")
                .nickname("attacker")
                .role(Role.USER)
                .build();
        memberRepository.save(attacker);

        String attackerUsername = "attacker";
        Long bookmarkIdOfTestMember = this.bookmarkA.getId(); // A 도서관 ID (소유자: myPageUser)

        // [When] & [Then]
        // 'attacker'가 'myPageUser'의 찜(bookmarkA)을 삭제하려고 시도
        assertThatThrownBy(() -> bookmarkService.deleteBookmark(bookmarkIdOfTestMember, attackerUsername))
                .isInstanceOf(IllegalArgumentException.class) // (또는 UsernameNotFoundException 등 적절한 예외)
                .hasMessageContaining("삭제 권한이 없거나 존재하지 않는 찜입니다.");
    }


}
