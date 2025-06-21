package com.blog.config.oauth;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

import com.blog.config.TokenAuthenticationFilter;
import com.blog.config.jwt.TokenProvider;
import com.blog.jwt.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;

@Configuration
@RequiredArgsConstructor
public class WebOAuthSecurityConfig {

    private final TokenProvider tokenProvider;

    @Bean
    public WebSecurityCustomizer configure() {
        // security 비활성화
        return (web) -> web.ignoring()
                .requestMatchers(toH2Console())
                .requestMatchers("/img/**", "/css/**", "/js/**");
    }

    @Bean
    // application context 초기화시 진행되는 로직
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // 토큰으로 인증을 하기 때문에 기존의 사용하던 폼로그인, 세션 비활성화
        http.csrf(configurer -> configurer.disable())
                .httpBasic(configurer -> configurer.disable())
                .formLogin(configurer -> configurer.disable())
                .logout(configurer -> configurer.disable())
                .sessionManagement(configurer -> configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // 헤더를 확인할 커스텀 filter 추가
        http.addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        // 토큰 재발급 url 은 인증 없이 접근을 허용 & 나머지 Api URL 은 인증을 필요
        http.authorizeHttpRequests(customizer ->
                customizer.requestMatchers("/api/token").permitAll()
                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().permitAll());

        // /api 로 시작하는 url 인 경우 인증 실패시 401 응답
        http.exceptionHandling((handlingCustomizer) -> handlingCustomizer
                .defaultAuthenticationEntryPointFor(
                        new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                        PathPatternRequestMatcher.withDefaults().matcher("/api/{*}"))
        );

        http.oauth2Login(auth -> auth.loginPage("/api/login")
                .authorizationEndpoint(point -> point.authorizationRequestRepository(
                        oAuth2AuthorizationRequestBasedOnCookieRepository()
                )));

        return http.build();
    }

    @Bean
    public TokenAuthenticationFilter tokenAuthenticationFilter() throws Exception {
        return new TokenAuthenticationFilter(tokenProvider);
    }

    @Bean
    public OAuth2AuthorizationRequestBasedOnCookieRepository oAuth2AuthorizationRequestBasedOnCookieRepository() {
        return new OAuth2AuthorizationRequestBasedOnCookieRepository();
    }
}
