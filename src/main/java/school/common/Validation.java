package school.common;

import java.util.Locale;

public final class Validation {
    private Validation() {
    }

    public static String text(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " is required");
        }
        return value.strip();
    }

    public static String id(String value, String field) {
        return text(value, field).toUpperCase(Locale.ROOT);
    }

    public static int positive(int value, String field) {
        if (value < 1) {
            throw new IllegalArgumentException(field + " must be positive");
        }
        return value;
    }
}
