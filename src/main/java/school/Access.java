package school;

public interface Access {
    boolean allows(Permission permission);
    String label();
    default void require(Permission permission) {
        if (!allows(permission)) throw new SecurityException("Permission required: " + permission);
    }
}
