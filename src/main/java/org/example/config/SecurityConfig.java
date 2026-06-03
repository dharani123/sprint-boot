package org.example.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // CORS: allow our React dev server to call the backend
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            // CSRF: disabled — CSRF attacks exploit browser cookies; we use JWT headers instead
            .csrf(csrf -> csrf.disable())

            // Sessions: STATELESS — the server never stores a session; every request is self-contained
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            .authorizeHttpRequests(auth -> auth
                // Public: anyone can register or login
                .requestMatchers("/api/auth/**").permitAll()
                // Public: H2 browser console (dev only — Spring Security blocks it by default)
                .requestMatchers("/h2-console/**").permitAll()
                // Cart and orders require a logged-in user
                .requestMatchers("/api/cart/**", "/api/orders/**").authenticated()
                // Products are public — anyone can browse the store
                .anyRequest().permitAll()
            )

            // H2 console uses <iframe> — frameOptions must be disabled so it renders
            .headers(headers -> headers.frameOptions(frame -> frame.disable()))

            // Register our JWT filter to run before Spring's default auth filter
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // BCrypt is the industry-standard password hashing algorithm.
    // It's deliberately slow and includes a random salt — brute-force attacks are very expensive.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        // allowedOriginPatterns supports wildcards and works with allowCredentials(true)
        config.setAllowedOriginPatterns(List.of("http://localhost:*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
