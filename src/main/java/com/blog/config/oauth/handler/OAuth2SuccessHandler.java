package com.blog.config.oauth.handler;

import com.blog.config.jwt.TokenProvider;
import com.blog.config.oauth.OAuth2AuthorizationRequestBasedOnCookieRepository;
import com.blog.jwt.domain.RefreshToken;
import com.blog.jwt.repository.RefreshTokenRepository;
import com.blog.oauth.util.CookieUtil;
import com.blog.user.entity.User;
import com.blog.user.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private static final Duration REFRESH_TOKEN_DURATION = Duration.ofDays(14);

    private final UserService userService;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final OAuth2AuthorizationRequestBasedOnCookieRepository oAuth2AuthorizationRequestBasedOnCookieRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        User user = userService.findByEmail(email);

        String refreshToken = tokenProvider.generateToken(user, REFRESH_TOKEN_DURATION);
        refreshTokenRepository.save(new RefreshToken(user.getId(), refreshToken));

        CookieUtil.addCookie(response, "refresh_token", refreshToken, (int) REFRESH_TOKEN_DURATION.toSeconds());

        String accessToken = tokenProvider.generateToken(user, Duration.ofDays(1));
        String targetUrl = getTargetUrl(accessToken);

        clearAuthenticationAttributes(request, response);
    }

    private String getTargetUrl(String token) {
        return UriComponentsBuilder.fromUriString("/api/articles")
                .queryParam("token", token)
                .build()
                .toUriString();
    }

    private void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        oAuth2AuthorizationRequestBasedOnCookieRepository.removeAuthorizationRequest(request, response);
    }
}
