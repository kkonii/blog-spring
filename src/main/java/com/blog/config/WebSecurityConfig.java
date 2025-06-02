package com.blog.config;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

import com.blog.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;

@Configuration
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final UserService userService;

    @Bean
    //security 기능 비활성화 시키기
    public WebSecurityCustomizer configure() {
        return (web) -> web.ignoring()
                .requestMatchers(toH2Console())
                .requestMatchers("/static/**");
    }
}
