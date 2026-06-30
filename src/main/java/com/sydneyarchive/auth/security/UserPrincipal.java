package com.sydneyarchive.auth.security;

import com.sydneyarchive.user.enums.Role;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
@Builder
public class UserPrincipal implements UserDetails {

    // 내부용 사용자 식별자 (DB PK)
    private final String userId;
    private final Role role;

    /**
     * Spring Security 에서 사용하는 권한 목록
     * Enum Role 을 문자열 권한("ROLE_USER") 형태로 제공
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    /**
     * OAuth2 기반 인증에서는 비밀번호를 사용하지 않음
     * Form 로그인 방식이 아니므로 null 반환
     */
    @Override
    public String getPassword() {
        return null;
    }

    /**
     * Spring Security 내부 식별용 값
     * OAuth2 + JWT 구조에서는 실질적 의미가 없지만, UserDetails 구현을 위해 PK 반환
     */
    @Override
    public String getUsername() {
        return userId;
    }

    /**
     * 계정 만료 여부
     * 만료 개념을 사용하지 않으므로 항상 true
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 계정 잠금 여부
     * 잠금 기능을 사용하지 않으므로 항상 true
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * 비밀번호 만료 여부
     * 비밀번호 기반 인증이 아니므로 항상 true
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * 계정 활성화 여부
     * 별도 비활성화 정책 없으므로 기본 true
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
}
