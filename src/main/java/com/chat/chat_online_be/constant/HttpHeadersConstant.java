package com.chat.chat_online_be.constant;

/**
 * Provides string constants for HTTP headers, used to determine the client's IP address.
 */
public final class HttpHeadersConstant {
    // Private constructor to prevent instantiation
    private HttpHeadersConstant() {}

    public static final String X_FORWARDED_FOR = "X-Forwarded-For";
    public static final String PROXY_CLIENT_IP = "Proxy-Client-IP";
    public static final String WL_PROXY_CLIENT_IP = "WL-Proxy-Client-IP";
    public static final String HTTP_CLIENT_IP = "HTTP_CLIENT_IP";
    public static final String HTTP_X_FORWARDED_FOR = "HTTP_X_FORWARDED_FOR";
}
