package com.chat.chat_online_be.config;

import com.chat.chat_online_be.entity.AuthorityEntity;
import com.chat.chat_online_be.entity.UserEntity;
import com.chat.chat_online_be.repository.RepositoryContainer;
import com.chat.chat_online_be.service.external.ExternalServiceContainer;
import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
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

    ExternalServiceContainer externalServiceContainer;
    RepositoryContainer repositoryContainer;

    /**
     * Constructor to initialize {@link DataInitializer} with dependencies.
     */
    @PostConstruct
    public void init() {
        // Set data default to m_authority
        var adminAuthor = initAuthority();

        if (!adminAuthor.isEmpty()) {
            initAdmin(adminAuthor);
        }
    }

    /**
     * Init default data for authority table
     */
    private List<AuthorityEntity> initAuthority() {
        // Create AuthorityEntity instances for admin and user roles
        var admin = AuthorityEntity.builder().name("ADMIN").build();
        var user = AuthorityEntity.builder().name("USER").build();

        // Save AuthorityEntity instances
        var authors = new ArrayList<AuthorityEntity>();

        // Save default authority to database if not exist
        synchronized (this) {
            if (!repositoryContainer.getAuthorityRepository().existsByName(admin.getName())) {
                var adminAuthor = repositoryContainer.getAuthorityRepository().save(admin);
                authors.add(adminAuthor);
            }

            if (!repositoryContainer.getAuthorityRepository().existsByName(user.getName())) {
                var userAuthor = repositoryContainer.getAuthorityRepository().save(user);
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
        var admin = UserEntity.builder()
                .username("admin")
                .email("admin@gmail.com")
                .password(externalServiceContainer.getPasswordEncoder().encode("123456"))
                .authorities(authors)
                .isOnline(true)
                .build();

        // Set the createdBy and modifiedBy fields for auditing purposes
        admin.setCreatedBy(1L);
        admin.setModifiedBy(1L);

        // Save admin user to database if not exist
        synchronized (this) {
            if (!repositoryContainer.getUserRepository().existsByEmail(admin.getEmail())) {
                repositoryContainer.getUserRepository().save(admin);
            }
        }
    }
}
