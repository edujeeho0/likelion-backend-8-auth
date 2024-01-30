package com.example.auth.service;

import com.example.auth.entity.CustomUserDetails;
import com.example.auth.entity.UserEntity;
import com.example.auth.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Slf4j
//@RequiredArgsConstructor
@Service
public class JpaUserDetailsManager implements UserDetailsManager {
    private final UserRepository userRepository;

    public JpaUserDetailsManager(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        // 오롯이 테스트 목적의 사용자를 추가하는 용도
        /*createUser(User.withUsername("user1")
                .password(passwordEncoder.encode("password1"))
                .build());
        createUser(User.withUsername("user2")
                .password(passwordEncoder.encode("password2"))
                .build());*/
        createUser(CustomUserDetails.builder()
                .username("user")
                .password(passwordEncoder.encode("password"))
                .email("user1@gmail.com")
                .phone("01012345678")
                .authorities("ROLE_USER")
                .build());

        createUser(CustomUserDetails.builder()
                .username("admin")
                .password(passwordEncoder.encode("password"))
                .email("admin@gmail.com")
                .phone("01012345678")
                .authorities("ROLE_ADMIN")
                .build());
    }

    @Override
    // formLogin 등 Spring Security 내부에서
    // 인증을 처리할때 사용하는 메서드이다.
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        Optional<UserEntity> optionalUser
                = userRepository.findByUsername(username);
        if (optionalUser.isEmpty())
            throw new UsernameNotFoundException(username);

        UserEntity userEntity = optionalUser.get();
        return CustomUserDetails.builder()
                .username(userEntity.getUsername())
                .password(userEntity.getPassword())
                .email(userEntity.getEmail())
                .phone(userEntity.getPhone())
                .authorities(userEntity.getAuthorities())
                .build();

        /*return User.withUsername(username)
                .password(optionalUser.get().getPassword())
                .build();*/
    }

    @Override
    // 편의를 위해 같은 규약으로 회원가입을 하는 메서드
    public void createUser(UserDetails user) {
        if (userExists(user.getUsername()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        try {
            CustomUserDetails userDetails =
                    (CustomUserDetails) user;
            UserEntity newUser = UserEntity.builder()
                    .username(userDetails.getUsername())
                    .password(userDetails.getPassword())
                    .email(userDetails.getEmail())
                    .phone(userDetails.getPhone())
                    .authorities(userDetails.getRawAuthorities())
                    .build();
            userRepository.save(newUser);
        } catch (ClassCastException e) {
            log.error("Failed Cast to: {}", CustomUserDetails.class);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        /*UserEntity userEntity = UserEntity.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .build();
        userRepository.save(userEntity);*/
    }

    @Override
    public boolean userExists(String username) {
        return userRepository.existsByUsername(username);
    }

    // 나중에...
    @Override
    public void updateUser(UserDetails user) {
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED);
    }

    @Override
    public void deleteUser(String username) {
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED);
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED);
    }
}
