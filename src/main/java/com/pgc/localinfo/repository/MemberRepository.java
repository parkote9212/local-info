package com.pgc.localinfo.repository;

import com.pgc.localinfo.LocalInfoApplication;
import com.pgc.localinfo.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    // Spring Data JPA의 '쿼리 메서드' 기능
    // 'findBy' + '[필드 이름]' -> SELECT * FROM member WHERE username = ? 쿼리를 자동 생성
    Optional<Member> findByUsername(String username);

    Optional<Member> findByNickname(String nickname);

}
