package com.chat.chat_online_be.repository;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Repository;

/**
 * Container for all repositories.
 * <p>
 * This class is annotated as a Spring repository and is autowired by Spring.
 * It provides getters for all repositories which are also annotated as Spring repositories.
 * The repositories are autowired by Spring using constructor injection.
 */
@Repository
@AllArgsConstructor
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RepositoryContainer {
    IUserRepository userRepository;
    IAuthorityRepository authorityRepository;
}
