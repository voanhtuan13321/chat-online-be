package com.chat.chat_online_be.config;

import com.chat.chat_online_be.security.JwtAuthenticationFilter;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * SecurityConfig class configures the security settings for the application.
 * It sets up a security filter chain to manage HTTP security, including
 * disabling CSRF and CORS, defining authorization rules, setting session
 * management policy to STATELESS, and adding a JWT authentication filter
 * before the UsernamePasswordAuthenticationFilter.
 * It also provides a bean for the AuthenticationManager using the given
 * AuthenticationConfiguration.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@SuppressWarnings("unused")
public class SecurityConfig {

    JwtAuthenticationFilter jwtAuthenticationFilter;
    AuthenticationProvider authenticationProvider;

    /**
     * A Spring Security filter chain that disables CSRF protection and
     * sets the session creation policy to STATELESS, and authorizes
     * HTTP requests to permit all to "/api/auth/**" and to require
     * authentication for all other requests, and adds a filter before
     * the {@code UsernamePasswordAuthenticationFilter} to handle JWT
     * authentication.
     *
     * @param http the {@code HttpSecurity} object to configure
     * @return a configured {@code SecurityFilterChain}
     * @throws Exception if an error occurs while building the filter chain
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        var publicUrls = new String[]{
                "/api/v1/auth/**",
                "/css/style.css",
                "/favicon.ico",
        };

        http.csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(publicUrls).permitAll()
                        // Uncomment the following lines to add role-based access control
                        //.requestMatchers("/api/v1/admin/**").hasRole("ADMIN") // Only allow ADMIN role to access
                        //.requestMatchers("/api/v1/user/**").hasAnyRole("USER", "ADMIN") // Allow both USER and ADMIN roles
                        .anyRequest().authenticated()
                )
                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    /**
     * Creates an {@link AuthenticationManager} bean that uses the
     * authentication configuration provided by the given
     * {@link AuthenticationConfiguration}.
     *
     * @param authConfig the authentication configuration to use
     * @return an {@link AuthenticationManager} instance
     * @throws Exception if an error occurs while creating the bean
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}