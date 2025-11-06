package com.pgc.localinfo.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {

//  권한 앞에 ROLE_접두사를 붙어야 합니다.
    USER("ROLE_USER", "일반사용자"),
    ADMIN("ROLE_ADMIN", "관리자");

    private final String key;
    private final String title;


}
