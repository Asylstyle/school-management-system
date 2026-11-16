package school.access;

public final class RegistrarAccess extends AccessDecorator {
    public RegistrarAccess(Access wrapped) {
        super(wrapped);
    }

    @Override
    public boolean allows(Permission permission) {
        if (permission == Permission.MANAGE_PEOPLE) {
            return true;
        }
        if (permission == Permission.MANAGE_COURSES) {
            return true;
        }
        if (permission == Permission.ENROLL) {
            return true;
        }
        return super.allows(permission);
    }

    @Override
    public String label() {
        return wrapped.label() + " + Registrar";
    }
}
