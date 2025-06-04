package com.blog.user.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserViewController {

    @GetMapping("/api/login")
    public String login() {
        //api/login 경로로 접근시 해당 메서드가 login.html 을 반환
        return "login";
    }

    @GetMapping("api/signup")
    public String signup() {
        //api/signup 경로로 접근시 해당 메서드가 signup.html 을 반환
        return "signup";
    }

    @GetMapping("api/home")
    public String home() {
        //api/home 경로로 접근시 해당 메서드가 home.html 을 반환
        return "home";
    }
}
