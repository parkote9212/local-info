package com.pgc.localinfo.service;

import com.pgc.localinfo.domain.LibraryTip;
import com.pgc.localinfo.domain.Member;
import com.pgc.localinfo.dto.TipCreateRequestDto;
import com.pgc.localinfo.dto.TipResponseDto;
import com.pgc.localinfo.repository.LibraryTipRepository;
import com.pgc.localinfo.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TipService {

    private final MemberRepository memberRepository;
    private final LibraryTipRepository tipRepository;

    /**
     * (팁 생성 C) 새로운 팁을 작성
     * @param username   로그인한 사용자 ID
     * @param requestDto 팁 생성 DTO
     * @return 생성된 팁의 ID
     */
    @Transactional
    public Long createTip(String username, TipCreateRequestDto requestDto) {
        // 1. username으로 Member 엔티티 조회
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));

        // 2. DTO -> Entity 변환 (이때 Member 엔티티를 함께 넣어줌)
        LibraryTip newTip = requestDto.toEntity(member);

        // 3. 저장
        LibraryTip savedTip = tipRepository.save(newTip);
        return savedTip.getId();
    }

    /**
     * (팁 조회 R) 특정 도서관의 팁 목록 조회
     * @param libraryAddress 도서관 주소
     * @param currentUsername (선택) 현재 로그인한 사용자 ID (본인 글 표시용)
     * @return 팁 DTO 목록
     */
    public List<TipResponseDto> getTipsByLibraryAddress(String libraryAddress, String currentUsername) {

        // 1. [15-2]에서 만든 쿼리 메서드(최신순)로 팁 목록 조회
        List<LibraryTip> tips = tipRepository.findByLibraryAddressOrderByCreatedAtDesc(libraryAddress);

        // 2. 팁 목록(엔티티)을 DTO 목록으로 변환
        return tips.stream()
                // fromEntity 호출 시, 팁의 주인(isOwner)인지 판단하기 위해 currentUsername을 넘겨줌
                .map(tip -> TipResponseDto.fromEntity(tip, currentUsername))
                .toList(); // (SonarLint 권장 사항 반영)
    }

    /**
     * (팁 수정 U) 팁 내용을 수정 (본인 확인)
     * @param tipId      수정할 팁 ID
     * @param newContent 새 팁 내용
     * @param username   로그인한 사용자 ID (소유권 확인용)
     */
    @Transactional
    public void updateTip(Long tipId, String newContent, String username) {
        // [TDD 3단계 - 로직 구현]

        // 1. username으로 Member 엔티티 조회
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));

        // 2. [15-2]에서 만든 쿼리로 '본인 소유의' 팁인지 조회
        LibraryTip tip = tipRepository.findByIdAndMember(tipId, member)
                .orElseThrow(() -> new IllegalArgumentException("수정 권한이 없거나 존재하지 않는 팁입니다."));

        // 3. (Dirty Checking) 엔티티의 내용만 변경
        //    @Transactional이 붙어있으므로, 메서드가 끝나면 JPA가 변경을 감지하여 UPDATE 쿼리를 실행
        tip.updateContent(newContent);
    }

    /**
     * (팁 삭제 D) 팁을 삭제 (본인 확인)
     * @param tipId    삭제할 팁 ID
     * @param username 로그인한 사용자 ID (소유권 확인용)
     */
    @Transactional
    public void deleteTip(Long tipId, String username) {
        // [TDD 3단계 - 로직 구현]

        // 1. username으로 Member 조회
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));

        // 2. '본인 소유의' 팁인지 조회
        LibraryTip tip = tipRepository.findByIdAndMember(tipId, member)
                .orElseThrow(() -> new IllegalArgumentException("삭제 권한이 없거나 존재하지 않는 팁입니다."));

        // 3. 소유권이 확인되었으므로 삭제
        tipRepository.delete(tip);
    }
}