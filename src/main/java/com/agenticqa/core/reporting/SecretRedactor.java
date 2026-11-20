package com.agenticqa.core.reporting;

import java.util.regex.Pattern;

/**
 * Redacts credentials, tokens and cookie values from diagnostic text.
 */
public final class SecretRedactor {

    private static final Pattern PASSWORD = Pattern.compile("(?i)(password|passwd|pwd)([\"'\\s:=]+)([^\\s\"'&,}]+)");
    private static final Pattern TOKEN = Pattern.compile("(?i)(token|authorization|api[_-]?key)([\"'\\s:=]+)([^\\s\"'&,}]+)");
    private static final Pattern COOKIE = Pattern.compile("(?i)(cookie|set-cookie)([\"'\\s:=]+)([^\\r\\n]+)");

    private SecretRedactor() {
    }

    public static String redact(String input) {
        if (input == null || input.isBlank()) {
            return input;
        }
        String result = PASSWORD.matcher(input).replaceAll("$1$2***REDACTED***");
        result = TOKEN.matcher(result).replaceAll("$1$2***REDACTED***");
        result = COOKIE.matcher(result).replaceAll("$1$2***REDACTED***");
        return result;
    }
}
