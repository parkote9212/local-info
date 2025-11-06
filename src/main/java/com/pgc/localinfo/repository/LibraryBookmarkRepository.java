package com.pgc.localinfo.repository;

import com.pgc.localinfo.domain.LibraryBookmark;
import com.pgc.localinfo.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LibraryBookmarkRepository extends JpaRepository<LibraryBookmark, Long> {


    // (정렬 기준: 가장 최근에 찜한 순서대로)
    List<LibraryBookmark> findByMemberOrderByIdDesc(Member member);

    // 2. (중복 찜 방지용) 특정 회원이 특정 주소의 도서관을 찜했는지 확인
    Optional<LibraryBookmark> findByMemberAndAddress(Member member, String address);

    // 3. (찜 삭제용) 특정 회원이 특정 북마크 ID를 소유했는지 확인
    Optional<LibraryBookmark> findByIdAndMember(Long id, Member member);
}