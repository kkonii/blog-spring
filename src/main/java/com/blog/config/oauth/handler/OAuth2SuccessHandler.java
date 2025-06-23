package com.blog.config.oauth.handler;

import com.blog.config.jwt.TokenProvider;
import com.blog.jwt.domain.RefreshToken;
import com.blog.jwt.repository.RefreshTokenRepository;
import com.blog.user.entity.User;
import com.blog.user.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserService userService;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        User user = userService.findByEmail(email);

        String refreshToken = tokenProvider.generateToken(user, Duration.ofDays(14));
        refreshTokenRepository.save(new RefreshToken(user.getId(), refreshToken));

        response.addCookie(new Cookie("refresh_token", refreshToken));
    }
}
