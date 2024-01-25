package com.example.auth;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RootController {
    @GetMapping
    public String root() {
        return "hello";
    }

    // 로그인을 안해도 no-auth에는 접근 가능
    @GetMapping("/no-auth")
    public String noAuth() {
        return "no auth success!";
    }

    // 로그인을 해야만 접근 가능
    @GetMapping("/require-auth")
    public String reAuth() {
        return "auth success!";
    }
}
