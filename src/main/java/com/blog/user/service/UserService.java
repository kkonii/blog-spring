package com.blog.user.service;

import com.blog.user.controller.dto.request.AddUserRequest;
import com.blog.user.entity.User;
import com.blog.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public Long save(AddUserRequest request) {
        String email = request.getEmail();
        String password = passwordEncoder.encode(request.getPassword());

        return userRepository.save(User.builder()
                .email(email)
                .password(password)
                .build()
        ).getId();
    }

    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected user"));
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected user"));
    }
}
