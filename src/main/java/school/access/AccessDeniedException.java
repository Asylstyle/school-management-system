package school.access;

public final class AccessDeniedException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public AccessDeniedException(Permission permission) {
        super("Permission required: " + permission);
    }
}
