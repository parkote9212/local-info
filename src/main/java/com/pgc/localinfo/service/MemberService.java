package com.pgc.localinfo.service;

import com.pgc.localinfo.domain.Member;
import com.pgc.localinfo.dto.MemberSignupRequestDto;
import com.pgc.localinfo.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 회원가입
     *
     * @param requestDto 회원가입 요청 DTO
     * @return 저장된 Member의 ID
     */

    @Transactional
    public Long signup(MemberSignupRequestDto requestDto) {

        //중복검증
        validateDuplicateMember(requestDto);

        // 암호화 및 엔티티 변환(엔티티변환은 DTO또는 서비스에서 처리)
        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());
        Member member = requestDto.toEntity(encodedPassword);

        //저장
        Member savedMember = memberRepository.save(member);

        return savedMember.getId();
    }

    private void validateDuplicateMember(MemberSignupRequestDto requestDto) {
        // 유저아이디 중복검사
        memberRepository.findByUsername(requestDto.getUsername())
                .ifPresent(m -> {
                    throw new IllegalStateException("이미 존재하는 아이디입니다.");
                });

        //닉네임 중복검사
        memberRepository.findByNickname(requestDto.getNickname())
                .ifPresent(n -> {
                    throw new IllegalStateException("이미 존재하는 닉네임입니다.");
                });
    }
}

