package com.chat.chat_online_be.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * Represents a user entity.
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class UserEntity extends AuditableEntity implements UserDetails, Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false, unique = true)
    String username;

    @Column(nullable = false)
    String password;

    @Column(nullable = false, unique = true)
    String email;

    @Column(name = "is_online", nullable = false)
    boolean isOnline = true;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_authorities",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "authority_id")
    )
    List<AuthorityEntity> authorities;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }


    /**
     * Returns the username used to authenticate the user.
     * <p>
     * The username is a unique identifier for the user, and is used to identify
     * the user when they log in.
     *
     * @return the username used to authenticate the user
     */
    @Override
    public String getUsername() {
        return username;
    }


    /**
     * Returns the password used to authenticate the user.
     * <p>
     * The password is used to authenticate the user when they log in.
     *
     * @return the password used to authenticate the user
     */
    @Override
    public String getPassword() {
        return password;
    }

    /**
     * Indicates whether the user's account is expired.
     * <p>
     * An expired account cannot be authenticated.
     * <p>
     * This method always returns {@code true}, indicating that the user's account
     * is never expired.
     *
     * @return {@code true} if the user's account is valid (ie non-expired),
     * {@code false} if it has expired
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }


    /**
     * Indicates whether the user is locked or unlocked.
     * <p>
     * A locked user cannot be authenticated.
     * <p>
     * This method always returns {@code true}, indicating that the user is never
     * locked.
     *
     * @return {@code true} if the user is unlocked, {@code false} if they are locked
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }


    /**
     * Indicates whether the user's credentials have expired.
     * <p>
     * Expired credentials prevent authentication.
     * <p>
     * This method always returns {@code true}, indicating that the user's credentials
     * never expire.
     *
     * @return {@code true} if the user's credentials are valid (ie non-expired),
     * {@code false} if they have expired
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
}