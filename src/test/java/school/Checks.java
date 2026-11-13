package school;

final class Checks {
    private static int count;
    static void equal(Object expected, Object actual) {
        count++;
        if (!java.util.Objects.equals(expected, actual))
            throw new AssertionError("Expected " + expected + ", got " + actual);
    }
    static void close(double expected, double actual) {
        count++;
        if (!Double.isFinite(actual) || Math.abs(expected - actual) > 0.000001)
            throw new AssertionError("Expected " + expected + ", got " + actual);
    }
    static void fails(Class<? extends Exception> type, Runnable action) {
        count++;
        try { action.run(); }
        catch (Exception e) {
            if (type.isInstance(e)) return;
            throw new AssertionError("Expected " + type.getSimpleName() + ", got " + e, e);
        }
        throw new AssertionError("Expected " + type.getSimpleName());
    }
    static int count() { return count; }
}
