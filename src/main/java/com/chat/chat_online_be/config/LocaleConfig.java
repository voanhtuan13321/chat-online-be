package com.chat.chat_online_be.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.Locale;

/**
 * Configures locale settings for the application.
 * This configuration class provides beans for message source and locale resolution.
 * It sets up a reloadable resource bundle message source with UTF-8 encoding and
 * resolves locales based on the Accept-Language HTTP header, defaulting to US English.
 */
@Configuration
@SuppressWarnings("unused")
public class LocaleConfig {
    /**
     * Creates a message source bean that loads resource bundles from the classpath using UTF-8 encoding.
     * The resource bundles are expected to be located in the "i18n" directory and have a basename of "messages".
     * The message source is reloadable, allowing it to be updated without requiring a restart of the application.
     *
     * @return a MessageSource instance
     */
    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:i18n/messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

    /**
     * Creates a locale resolver bean that uses the Accept-Language header in the HTTP request
     * to determine the user's preferred locale.
     * If the Accept-Language header is not present in the request, it defaults to US English (en_US).
     *
     * @return a LocaleResolver instance
     */
    @Bean
    public LocaleResolver localeResolver() {
        AcceptHeaderLocaleResolver localeResolver = new AcceptHeaderLocaleResolver();
        localeResolver.setDefaultLocale(Locale.US);
        return localeResolver;
    }
}
