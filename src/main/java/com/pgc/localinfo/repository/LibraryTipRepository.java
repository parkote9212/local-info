package com.pgc.localinfo.repository;

import com.pgc.localinfo.domain.LibraryTip;
import com.pgc.localinfo.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LibraryTipRepository extends JpaRepository<LibraryTip, Long> {

    // 1. (도서관 상세페이지용) 특정 도서관의 '주소(address)'로 모든 팁을 최신순으로 조회
    List<LibraryTip> findByLibraryAddressOrderByCreatedAtDesc(String libraryAddress);

    // 2. (마이페이지용) 특정 회원이 작성한 모든 팁을 최신순으로 조회
    List<LibraryTip> findByMemberOrderByCreatedAtDesc(Member member);

    // 3. (수정/삭제용) 특정 팁 ID와 회원 정보로 '본인 소유의' 팁인지 확인 (보안)
    Optional<LibraryTip> findByIdAndMember(Long id, Member member);
}