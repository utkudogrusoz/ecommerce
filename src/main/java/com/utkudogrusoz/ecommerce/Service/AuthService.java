package com.utkudogrusoz.ecommerce.Service;

import com.utkudogrusoz.ecommerce.Dto.request.LoginRequest;
import com.utkudogrusoz.ecommerce.Model.UserModel;
import com.utkudogrusoz.ecommerce.Repository.jpa.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    public AuthService(
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserModel signup(LoginRequest request) {
        UserModel user = new UserModel.Builder()
                .username(request.username())
                .email("utku@gmail.com")
                .password(passwordEncoder.encode(request.password()))
                .build();

        return userRepository.save(user);

    }

    public UserModel authenticate(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );

        return userRepository.findByUsername(request.username())
                .orElseThrow();
    }

    public UserModel refreshAccessToken(String username) {

        return userRepository.findByUsername(username).orElseThrow();


    }
}