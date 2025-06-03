package com.blog.config;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

import com.blog.user.service.UserDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final UserDetailService userService;

    @Bean
    //security 기능 비활성화 시키기
    public WebSecurityCustomizer configure() {
        return (web) -> web.ignoring()
                .requestMatchers(toH2Console())
                .requestMatchers("/static/**");
    }

    // 특정 HTTP 요청에 대한 웹 기반 보안을 구성
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        HttpSecurity security = http.authorizeHttpRequests(auth ->
                auth.requestMatchers("/login", "/signup", "/user")
                        .permitAll()
                        .anyRequest().authenticated()
        );

        //로그인 설정
        security.formLogin(auth -> auth.loginPage("/login")
                .defaultSuccessUrl("/api/articles"));

        //로그아웃 설정
        security.logout(auth -> auth.logoutSuccessUrl("/login")
                .invalidateHttpSession(true));

        //csrf 비활성화
        return security.csrf(AbstractHttpConfigurer::disable).build();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            HttpSecurity http, BCryptPasswordEncoder bCryptPasswordEncoder, UserDetailService userDetailService)
            throws Exception {

        AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);

        builder
                .userDetailsService(userService)
                .passwordEncoder(bCryptPasswordEncoder);

        return builder.build();
    }
}
