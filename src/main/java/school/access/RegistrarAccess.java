package school.access;

public final class RegistrarAccess extends AccessDecorator {
    public RegistrarAccess(Access wrapped) {
        super(wrapped);
    }

    @Override
    public boolean allows(Permission permission) {
        return switch (permission) {
            case MANAGE_PEOPLE, MANAGE_COURSES, ENROLL -> true;
            default -> super.allows(permission);
        };
    }

    @Override
    public String label() {
        return wrapped.label() + " + Registrar";
    }
}
