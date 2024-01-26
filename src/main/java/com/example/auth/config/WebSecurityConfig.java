package com.example.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

// @Bean을 비롯해서 여러 설정을 하기 위한 Bean 객체
@Configuration
public class WebSecurityConfig {

    // 메서드의 결과를 Bean 객체로 관리해주는 어노테이션
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {
        http
        .csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(
                // /no-auth로 오는 요청은 모두 허가
                auth -> auth
                        // 어떤 경로에 대한 설정인지
                        .requestMatchers(
                                "/no-auth",
                                "/users/home"
                        )
                        // 이 경로에 도달할 수 있는 사람에 대한 설정(모두)
                        .permitAll()
                        .requestMatchers("/users/my-profile")
                        .authenticated()
                        .requestMatchers(
                                "/users/login",
                                "/users/register"
                        )
                        .anonymous()
                        .anyRequest()
                        .authenticated()
        )
        // html form 요소를 이용해 로그인을 시키는 설정
        .formLogin(
                formLogin -> formLogin
                        // 어떤 경로(URL)로 요청을 보내면
                        // 로그인 페이지가 나오는지
                        .loginPage("/users/login")
                        // 아무 설정 없이 로그인에 성공한 뒤
                        // 이동할 URL
                        .defaultSuccessUrl("/users/my-profile")
                        // 실패시 이동할 URL
                        .failureUrl("/users/login?fail")
        )
        // 로그아웃 설정
        .logout(
                logout -> logout
                        // 어떤 경로(URL)로 요청을 보내면 로그아웃이 되는지
                        // (사용자의 세션을 삭제할지)
                        .logoutUrl("/users/logout")
                        // 로그아웃 성공시 이동할 페이지
                        .logoutSuccessUrl("/users/home")
        )
        ;

        return http.build();
    }

    @Bean
    // 비밀번호 암호화 클래스
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

//    @Bean
    // 사용자 정보 관리 클래스
    public UserDetailsManager userDetailsManager(
        PasswordEncoder passwordEncoder
    ) {
        // 사용자 1
        UserDetails user1 = User.withUsername("user1")
                .password(passwordEncoder.encode("password1"))
                .build();
        // Spring Security에서 기본으로 제공하는,
        // 메모리 기반 사용자 관리 클래스 + 사용자 1
        return new InMemoryUserDetailsManager(user1);
    }
}
