package com.pgc.localinfo.service;

import com.pgc.localinfo.domain.LibraryBookmark;
import com.pgc.localinfo.domain.Member;
import com.pgc.localinfo.dto.BookmarkRequestDto;
import com.pgc.localinfo.dto.BookmarkResponseDto;
import com.pgc.localinfo.repository.LibraryBookmarkRepository;
import com.pgc.localinfo.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookmarkService {

    private final MemberRepository memberRepository;
    private final LibraryBookmarkRepository libraryBookmarkRepository;

    /**
     * (마이페이지) 현재 로그인한 사용자의 모든 찜 목록 조회
     *
     * @param username (Spring Security에서 가져온 사용자 ID)
     * @return 찜 목록 DTO 리스트
     */

    public List<BookmarkResponseDto> getMyBookmarks(String username) {

        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));

        List<LibraryBookmark> bookmarks = libraryBookmarkRepository.findByMemberOrderByIdDesc(member);

        return bookmarks.stream()
                .map(BookmarkResponseDto::fromEntity)
                .toList();
    }

    /**
     * (찜하기) 새로운 북마크를 추가
     * @param username 로그인한 사용자 ID
     * @param requestDto 찜할 도서관 정보 DTO
     * @return 저장된 Bookmark의 ID
     */
    @Transactional // DB에 CUD가 발생하므로 @Transactional 추가
    public Long addBookmark(String username, BookmarkRequestDto requestDto) {
// [TDD 3단계 - 로직 구현]

        // 1. username으로 Member 엔티티 조회
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));

        // 2. 이미 찜한 도서관인지 중복 검사 (findByMemberAndAddress 쿼리 사용)
        libraryBookmarkRepository.findByMemberAndAddress(member, requestDto.getAddress())
                .ifPresent(bookmark -> { // 찜한 기록(bookmark)이 존재한다면(ifPresent)
                    throw new IllegalStateException("이미 찜한 도서관입니다.");
                });

        // 3. DTO -> Entity 변환
        LibraryBookmark newBookmark = requestDto.toEntity(member);

        // 4. 저장(save) 및 ID 반환
        LibraryBookmark savedBookmark = libraryBookmarkRepository.save(newBookmark);
        return savedBookmark.getId();
    }

    /**
     * (찜 삭제) 북마크를 삭제 (반드시 본인 소유인지 확인)
     * @param bookmarkId 삭제할 북마크 ID
     * @param username   로그인한 사용자 ID (소유권 확인용)
     */
    @Transactional
    public void deleteBookmark(Long bookmarkId, String username) {
        // 1. username으로 Member 엔티티 조회 (회원이 존재하는지 확인)
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));

        // 2. Member와 bookmarkId로 '본인 소유의' 찜인지 조회
        //    (findByIdAndMember: [11-2]에서 만든 쿼리 메서드)
        LibraryBookmark bookmark = libraryBookmarkRepository.findByIdAndMember(bookmarkId, member)
                .orElseThrow(() -> new IllegalArgumentException("삭제 권한이 없거나 존재하지 않는 찜입니다."));

        // 3. 찜 기록이 존재하고, 본인 소유임이 확인되었으므로 삭제
        libraryBookmarkRepository.delete(bookmark);
    }


}
