package com.chat.chat_online_be.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Enables JPA auditing, which provides automatic updating of certain fields in entities.
 */
@Configuration
@EnableJpaAuditing
@SuppressWarnings("unused")
public class JpaAuditingConfig {
}
