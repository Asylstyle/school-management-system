package school;

final class Validation {
    private Validation() {}
    static String text(String value, String field) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(field + " is required");
        return value.strip();
    }
    static int positive(int value, String field) {
        if (value < 1) throw new IllegalArgumentException(field + " must be positive");
        return value;
    }
}
