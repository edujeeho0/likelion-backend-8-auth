package com.example.auth;

import com.example.auth.entity.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserDetailsManager manager;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationFacade authFacade;

    @GetMapping("/home")
    public String home() {
        log.info(SecurityContextHolder.getContext().getAuthentication().getName());
        log.info(authFacade.getAuth().getName());

        return "index";
    }

    @GetMapping("/login")
    public String loginForm() {
        return "login-form";
    }

    // 로그인 한 후에 내가 누군지 확인하기 위한
    @GetMapping("/my-profile")
    public String myProfile(
            Authentication authentication,
            Model model
    ) {
        model.addAttribute("username", authentication.getName());
        log.info(authentication.getName());
//        log.info(((User) authentication.getPrincipal()).getPassword());
        log.info(((CustomUserDetails) authentication.getPrincipal()).getPassword());
        log.info(((CustomUserDetails) authentication.getPrincipal()).getPhone());
        return "my-profile";
    }

    // 회원가입 화면
    @GetMapping("/register")
    public String signUpForm() {
        return "register-form";
    }

    @PostMapping("/register")
    public String signUpRequest(
            @RequestParam("username")
            String username,
            @RequestParam("password")
            String password,
            @RequestParam("password-check")
            String passwordCheck
    ) {
        if (password.equals(passwordCheck))
            /*manager.createUser(User.withUsername(username)
                    .password(passwordEncoder.encode(password))
                    .build());*/
            manager.createUser(CustomUserDetails.builder()
                            .username(username)
                            .password(passwordEncoder.encode(password))
                    .build());

        // 회원가입 성공 후 로그인 페이지로
        return "redirect:/users/login";
    }
}
