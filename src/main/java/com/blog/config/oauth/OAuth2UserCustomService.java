package com.blog.config.oauth;

import com.blog.user.entity.User;
import com.blog.user.repository.UserRepository;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OAuth2UserCustomService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 요청을 바탕으로 유저 정보를 담은 객체를 반환
        OAuth2User user = super.loadUser(userRequest);
        saveOrUpdate(user);

        return user;
    }

    // 유저가 있으면 업데이트, 없으면 새로 생성하여 db에 저장한다
    private User saveOrUpdate(OAuth2User oauthUser) {
        Map<String, Object> attributes = oauthUser.getAttributes();

        String email = (String) attributes.get("email");
        String nickname = (String) attributes.get("nickname");

        User user = userRepository.findByEmail(email)
                .map(entity -> entity.update(nickname))
                .orElse(User.builder()
                        .email(email)
                        .nickname(nickname)
                        .build());

        return userRepository.save(user);
    }
}
