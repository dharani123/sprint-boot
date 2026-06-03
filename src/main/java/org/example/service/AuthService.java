package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.config.JwtUtil;
import org.example.dto.AuthResponseDTO;
import org.example.dto.LoginRequestDTO;
import org.example.dto.RegisterRequestDTO;
import org.example.exception.DuplicateEmailException;
import org.example.exception.DuplicateMobileException;
import org.example.exception.InvalidCredentialsException;
import org.example.model.User;
import org.example.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthResponseDTO register(RegisterRequestDTO request) {
        log.info("→ entering register: email={}", request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException("Email already registered: " + request.getEmail());
        }
        if (userRepository.existsByMobileNumber(request.getMobileNumber())) {
            throw new DuplicateMobileException("Mobile number already registered: " + request.getMobileNumber());
        }

        // encode() hashes the password with BCrypt — the plain-text password is never stored
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .mobileNumber(request.getMobileNumber())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        userRepository.save(user);
        String token = jwtUtil.generateToken(user.getEmail());

        log.info("← returning register: user created email={}", user.getEmail());
        return new AuthResponseDTO(token, user.getName(), user.getEmail());
    }

    public AuthResponseDTO login(LoginRequestDTO request) {
        log.info("→ entering login: identifier={}", request.getIdentifier());

        // Try to find the user by email first, then by mobile number
        // .or() is a Java Optional method — try the second supplier only if the first is empty
        User user = userRepository.findByEmail(request.getIdentifier())
                .or(() -> userRepository.findByMobileNumber(request.getIdentifier()))
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email/mobile or password"));

        // matches() rehashes the input and compares — never decrypts
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid email/mobile or password");
        }

        String token = jwtUtil.generateToken(user.getEmail());
        log.info("← returning login: user authenticated email={}", user.getEmail());
        return new AuthResponseDTO(token, user.getName(), user.getEmail());
    }
}
