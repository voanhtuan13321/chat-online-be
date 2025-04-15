package com.chat.chat_online_be.constant;

/**
 * Provides string constants for exception class names, used to determine the
 * type of exception and provide appropriate error messages.
 */
public final class ClassNamesConstant {
    // Private constructor to prevent instantiation
    private ClassNamesConstant() {}

    public static final String EXPIRED_JWT_EXCEPTION = "ExpiredJwtException";
    public static final String UNSUPPORTED_JWT_EXCEPTION = "UnsupportedJwtException";
    public static final String MALFORMED_JWT_EXCEPTION = "MalformedJwtException";
    public static final String ILLEGAL_ARGUMENT_EXCEPTION = "IllegalArgumentException";
    public static final String USERNAME_NOT_FOUND_EXCEPTION = "UsernameNotFoundException";
    public static final String AUTHENTICATION_EXCEPTION = "AuthenticationException";
}
