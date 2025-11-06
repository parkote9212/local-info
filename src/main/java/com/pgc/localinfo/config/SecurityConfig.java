package com.pgc.localinfo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity

                // [중요] URL별 접근 권한 설정
                .authorizeHttpRequests(authz -> authz
                        // 1. 누구나 접근 가능한 URL (if, else if...)
                        .requestMatchers("/", "/members/signup", "/members/login",
                        "/libraries/**","/css/**", "/js/**", "/images/**").permitAll()

                        // 2. 그 외 모든 요청 (else) - 반드시 맨 마지막에 와야 합니다!
                        .anyRequest().authenticated()
                )

                // 폼(Form) 기반 로그인 설정
                .formLogin(formLogin -> formLogin
                        .loginPage("/members/login") // 로그인 페이지 URL
                        .defaultSuccessUrl("/", true) // 성공 시 메인으로
                        .permitAll() // 로그인 페이지는 누구나 접근 가능
                )



                // 로그아웃 설정
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                );

        return httpSecurity.build();
    }
}