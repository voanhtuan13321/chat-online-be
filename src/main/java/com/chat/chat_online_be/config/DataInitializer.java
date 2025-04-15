package com.chat.chat_online_be.config;

import com.chat.chat_online_be.entity.AuthorityEntity;
import com.chat.chat_online_be.entity.UserEntity;
import com.chat.chat_online_be.repository.IAuthorityRepository;
import com.chat.chat_online_be.repository.IUserRepository;
import com.chat.chat_online_be.service.internal.IAuthenticationService;
import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is responsible for initializing default data in the application.
 * It sets up initial authorities and admin users in the database if they do not already exist.
 * The initialization is triggered automatically after the bean's construction.
 */
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@SuppressWarnings("unused")
public class DataInitializer {

    PasswordEncoder passwordEncoder;
    IAuthorityRepository authorityRepository;
    IUserRepository userRepository;

    /**
     * Constructor to initialize {@link DataInitializer} with dependencies.
     */
    @PostConstruct
    public void init() {
        // Set data default to m_authority
        List<AuthorityEntity> adminAuthor = initAuthority();

        if (!adminAuthor.isEmpty()) {
            initAdmin(adminAuthor);
        }
    }

    /**
     * Init default data for authority table
     */
    private List<AuthorityEntity> initAuthority() {
        // Create AuthorityEntity instances for admin and user roles
        AuthorityEntity admin = AuthorityEntity.builder().name("ADMIN").build();
        AuthorityEntity user = AuthorityEntity.builder().name("USER").build();

        // Save AuthorityEntity instances
        List<AuthorityEntity> authors = new ArrayList<>();

        // Save default authority to database if not exist
        synchronized (this) {
            if (!authorityRepository.existsByName(admin.getName())) {
                AuthorityEntity adminAuthor = authorityRepository.save(admin);
                authors.add(adminAuthor);
            }

            if (!authorityRepository.existsByName(user.getName())) {
                AuthorityEntity userAuthor = authorityRepository.save(user);
                authors.add(userAuthor);
            }
        }

        // Return created authorities
        return authors;
    }

    /**
     * Init default data for authority table
     */
    private void initAdmin(List<AuthorityEntity> authors) {
        // Create admin user instance
        UserEntity admin = UserEntity.builder()
                .username("admin")
                .email("admin@gmail.com")
                .password(passwordEncoder.encode("123456"))
                .authorities(authors)
                .isOnline(true)
                .build();

        // Set the createdBy and modifiedBy fields for auditing purposes
        admin.setCreatedBy(1L);
        admin.setModifiedBy(1L);

        // Save admin user to database if not exist
        synchronized (this) {
            if (!userRepository.existsByEmail(admin.getEmail())) {
                userRepository.save(admin);
            }
        }
    }
}
