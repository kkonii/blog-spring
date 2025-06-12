package com.blog.config;

import com.blog.config.jwt.JwtProperties;
import com.blog.config.jwt.TokenProvider;
import com.blog.user.entity.User;
import com.blog.user.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import java.time.Duration;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TokenProviderTest {

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtProperties jwtProperties;

    @Test
    @DisplayName("[Success] 유저 정보와 만료 기간을 전달하여 토큰을 생성하는 데에 성공한다.")
    void generateToken() {
        User testUser = userRepository.save(User.builder()
                .email("user@gmail.com")
                .password("123456789")
                .build());

        String token = tokenProvider.generateToken(testUser, Duration.ofDays(14L));

        Long userId = Jwts.parserBuilder()
                .setSigningKey(jwtProperties.getSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("id", Long.class);

        Assertions.assertThat(userId).isEqualTo(testUser.getId());
    }
}
