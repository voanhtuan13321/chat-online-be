package com.chat.chat_online_be.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.GrantedAuthority;

/**
 * Represents a granted authority (e.g. ROLE_ADMIN, ROLE_USER)
 */
@Entity
@Table(name = "m_authorities")
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class AuthorityEntity implements GrantedAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false, unique = true)
    String name;

    public AuthorityEntity(String name) {
        this.name = name;
    }

    /**
     * @return the authority of this authority instance
     */
    @Override
    public String getAuthority() {
        return name;
    }
}