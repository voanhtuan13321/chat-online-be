package com.chat.chat_online_be.config;

import com.chat.chat_online_be.repository.RepositoryContainer;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Provides necessary beans for user authentication and password handling in the application.
 */
@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@SuppressWarnings("unused")
public class ApplicationConfig {

    RepositoryContainer repository;

    /**
     * Configures the {@link ApplicationConfig} class with necessary beans
     * for user authentication and password encoding.
     * <p>
     * This class provides beans for {@link AuthenticationProvider},
     * {@link UserDetailsService}, and {@link PasswordEncoder} to support
     * user authentication and password handling in the application.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        var authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Creates an {@link AuthenticationProvider} bean that uses the
     * {@link DaoAuthenticationProvider} to authenticate users based
     * on the given {@link UserDetailsService} and {@link PasswordEncoder}
     * instances.
     *
     * @return an {@link AuthenticationProvider} instance
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return email -> repository
                .getUserRepository()
                .findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("email %s not found", email)));
    }

    /**
     * Creates a {@link PasswordEncoder} bean that uses the BCrypt
     * hashing algorithm to encode passwords.
     *
     * @return a {@link PasswordEncoder} instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
