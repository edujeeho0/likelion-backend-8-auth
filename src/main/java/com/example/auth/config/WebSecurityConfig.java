package com.example.auth.config;

import com.example.auth.jwt.JwtTokenFilter;
import com.example.auth.jwt.JwtTokenUtils;
import com.example.auth.oauth.OAuth2SuccessHandler;
import com.example.auth.oauth.OAuth2UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;

// @Bean을 비롯해서 여러 설정을 하기 위한 Bean 객체
@Configuration
@RequiredArgsConstructor
public class WebSecurityConfig {
    private final JwtTokenUtils jwtTokenUtils;
    private final UserDetailsManager manager;
    private final OAuth2UserServiceImpl oAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;

    // 메서드의 결과를 Bean 객체로 관리해주는 어노테이션
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {
        http
                // csrf 보안 해제
                .csrf(AbstractHttpConfigurer::disable)
                // url에 따른 요청 인가
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/no-auth",
                                "/users/home",
                                "/tests",
                                "/token/issue",
                                "/token/validate"
                        )
                        .permitAll()

                        .requestMatchers(HttpMethod.GET, "/articles")
                        .permitAll()

                        .requestMatchers(HttpMethod.POST, "/articles")
                        .authenticated()

                        .requestMatchers("/users/my-profile")
                        .authenticated()

                        .requestMatchers(
                                "/users/login",
                                "/users/register"
                        )
                        .anonymous()
                        // ROLE에 따른 접근 설정
                        .requestMatchers("/auth/user-role")
                        .hasAnyRole("USER", "ADMIN")

                        .requestMatchers("/auth/admin-role")
                        .hasRole("ADMIN")

                        // AUTHORITY에 따른 접근 설정
                        .requestMatchers("/auth/read-authority")
                        .hasAnyAuthority("READ_AUTHORITY", "WRITE_AUTHORITY")

                        .requestMatchers("/auth/write-authority")
                        .hasAuthority("WRITE_AUTHORITY")

                        .anyRequest()
                        .permitAll()
                )

                .oauth2Login(oauth2Login -> oauth2Login
                        .loginPage("/users/login")
                        .successHandler(oAuth2SuccessHandler)
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(oAuth2UserService))
                )

                // JWT를 사용하기 때문에 보안 관련 세션 해제
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // JWT 필터를 권한 필터 앞에 삽입
                .addFilterBefore(
                        new JwtTokenFilter(
                                jwtTokenUtils,
                                manager
                        ),
                        AuthorizationFilter.class
                )
        ;
        // JWT 이전
        /*
        http
        .csrf(AbstractHttpConfigurer::disable)
        // Security 5 까지
//        .authorizeHttpRequests()
//                .requestMatchers("")
//                .permitAll()
//        .and()
        .authorizeHttpRequests(
                // /no-auth로 오는 요청은 모두 허가
                auth -> auth
                        // 어떤 경로에 대한 설정인지
                        .requestMatchers(
                                "/no-auth",
                                "/users/home",
                                "/tests",
                                "/token/issue",
                                "/token/validate"
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
                        // .anyRequest().permitAll()
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
        // 특정 필터 앞에 나만의 필터를 넣는다.
        .addFilterBefore(
                new AllAuthenticatedFilter(),
                AuthorizationFilter.class
        )
        ;*/

        return http.build();
    }

    /*@Bean
    // 비밀번호 암호화 클래스
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }*/

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
