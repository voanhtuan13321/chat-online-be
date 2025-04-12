package com.chat.chat_online_be.service.external;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@SuppressWarnings("unused")
public class MessageSourceService {

    MessageSource messageSource;

    /**
     * Retrieve the message for the given key. The message is looked up with the locale
     * of the current thread. If no message is found, a {@link MessageSourceResolvable} is
     * used with the specified key as the only message code.
     *
     * @param key the message key
     * @return the message associated with the given key
     * @see org.springframework.context.MessageSource#getMessage(String, Object[], Locale)
     */
    public String getMessage(String key) {
        return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
    }

    /**
     * Retrieve the message for the given key in the given locale. If no message is found,
     * a {@link MessageSourceResolvable} is used with the specified key as the only message
     * code.
     *
     * @param key    the message key
     * @param locale the locale in which to retrieve the message
     * @return the message associated with the given key
     * @see org.springframework.context.MessageSource#getMessage(String, Object[], Locale)
     */
    public String getMessage(String key, Locale locale) {
        return messageSource.getMessage(key, null, locale);
    }

    /**
     * Retrieve the message for the given key. The message is looked up with the locale
     * of the current thread. If no message is found, a {@link MessageSourceResolvable} is
     * used with the specified key as the only message code.
     *
     * @param key the message key
     * @param args the array of message arguments, or {@code null} if none
     * @return the message associated with the given key
     * @see org.springframework.context.MessageSource#getMessage(String, Object[], Locale)
     */
    public String getMessage(String key, @Nullable Object[] args) {
        return messageSource.getMessage(key, args, LocaleContextHolder.getLocale());
    }

    /**
     * Retrieve the message for the given key in the given locale, using the
     * given array of objects as message arguments.
     *
     * @param key    the message key
     * @param args   the array of message arguments
     * @param locale the locale in which to retrieve the message
     * @return the message associated with the given key
     * @see org.springframework.context.MessageSource#getMessage(String, Object[], Locale)
     */
    public String getMessage(String key, @Nullable Object[] args, Locale locale) {
        return messageSource.getMessage(key, args, locale);
    }
}
