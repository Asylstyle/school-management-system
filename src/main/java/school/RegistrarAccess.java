package school;

public final class RegistrarAccess extends AccessDecorator {
    public RegistrarAccess(Access wrapped) { super(wrapped); }
    @Override public boolean allows(Permission p) {
        return p == Permission.MANAGE_PEOPLE || p == Permission.MANAGE_COURSES || p == Permission.ENROLL || super.allows(p);
    }
    @Override public String label() { return wrapped.label() + " + Registrar"; }
}
