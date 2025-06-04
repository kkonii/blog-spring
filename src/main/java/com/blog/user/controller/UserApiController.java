package com.blog.user.controller;

import com.blog.user.controller.dto.request.AddUserRequest;
import com.blog.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class UserApiController {

    private final UserService userService;

    @PostMapping("/api/user")
    public String signUp(AddUserRequest request) {
        userService.save(request);

        return "redirect:/api/login";
    }
}
