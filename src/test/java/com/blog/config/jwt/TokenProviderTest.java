package com.blog.config.jwt;

import static org.assertj.core.api.Assertions.assertThat;

import com.blog.user.entity.User;
import com.blog.user.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import java.time.Duration;
import java.util.Date;
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

        assertThat(userId).isEqualTo(testUser.getId());
    }

    @Test
    @DisplayName("[Exception] 만료된 토큰인 경우 유효성을 검증하는 데에 실패한다.")
    void validateInvalidToken() {
        String token = JwtFactory.builder()
                .expiration(new Date(new Date().getTime() - Duration.ofDays(7).toMillis()))
                .build()
                .createToken(jwtProperties);

        boolean result = tokenProvider.validateToken(token);

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("[Success] 유효한 토큰일 경우 유효성 검증하는 데에 성공한다.")
    void validateCorrectToken() {
        String token = JwtFactory.withDefaultValues().createToken(jwtProperties);

        boolean result = tokenProvider.validateToken(token);

        assertThat(result).isTrue();
    }
}
