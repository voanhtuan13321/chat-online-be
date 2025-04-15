package com.chat.chat_online_be.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * Utility class for extracting device information from HTTP requests.
 * This class provides methods to analyze User-Agent headers and determine
 * operating system and browser information.
 */
public final class DeviceInfoUtil {

    // Private constructor to prevent instantiation
    private DeviceInfoUtil() { }

    // Constants for device detection
    private static final String UNKNOWN_DEVICE = "Unknown Device";
    private static final String UNKNOWN_OS = "Unknown OS";
    private static final String UNKNOWN_BROWSER = "Unknown Browser";

    /**
     * Extracts device information from User-Agent and other headers.
     *
     * @param request HTTP servlet request
     * @return A string containing device information (OS and browser)
     */
    public static String extractDeviceInfo(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");

        if (!StringUtils.hasText(userAgent)) {
            return UNKNOWN_DEVICE;
        }

        String os = detectOperatingSystem(userAgent);
        String browser = detectBrowser(userAgent);

        return String.format("%s - %s", os, browser);
    }

    /**
     * Detects the operating system from the User-Agent string.
     *
     * @param userAgent The User-Agent header value
     * @return The name of the detected operating system
     */
    private static String detectOperatingSystem(String userAgent) {
        // Define OS detection with priority order
        List<Map.Entry<String, String>> osDetectionRules = List.of(
                Map.entry("iPhone", "iOS"),
                Map.entry("iPad", "iOS"),
                Map.entry("Android", "Android"),
                Map.entry("Mac OS X", "macOS"),
                Map.entry("Windows", "Windows"),
                Map.entry("Linux", "Linux")
        );

        // Check operating systems in priority order
        return osDetectionRules.stream()
                .filter(entry -> userAgent.contains(entry.getKey()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(UNKNOWN_OS);
    }

    /**
     * Detects the browser from the User-Agent string.
     * Handles special cases like Chrome vs Safari and Edge vs Chrome.
     *
     * @param userAgent The User-Agent header value
     * @return The name of the detected browser
     */
    private static String detectBrowser(String userAgent) {
        // Define browser detection with priority order
        List<Map.Entry<String, String>> browserDetectionRules = List.of(
                Map.entry("Edg", "Edge"),
                Map.entry("Chrome", "Chrome"),
                Map.entry("Firefox", "Firefox"),
                Map.entry("Safari", "Safari"),
                Map.entry("MSIE", "Internet Explorer"),
                Map.entry("Trident", "Internet Explorer")
        );

        // Check browsers in priority order
        return browserDetectionRules.stream()
                .filter(entry -> userAgent.contains(entry.getKey()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(UNKNOWN_BROWSER);
    }
}