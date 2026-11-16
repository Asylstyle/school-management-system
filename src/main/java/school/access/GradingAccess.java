package school.access;

public final class GradingAccess extends AccessDecorator {
    public GradingAccess(Access wrapped) {
        super(wrapped);
    }

    @Override
    public boolean allows(Permission permission) {
        if (permission == Permission.RECORD_GRADES) {
            return true;
        }
        return super.allows(permission);
    }

    @Override
    public String label() {
        return wrapped.label() + " + Grading";
    }
}
