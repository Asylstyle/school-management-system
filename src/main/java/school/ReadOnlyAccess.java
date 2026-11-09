package school;

public final class ReadOnlyAccess implements Access {
    @Override public boolean allows(Permission permission) { return permission == Permission.VIEW; }
    @Override public String label() { return "Viewer"; }
}
