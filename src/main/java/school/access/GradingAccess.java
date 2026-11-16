package school.access;

public final class GradingAccess extends AccessDecorator {
    public GradingAccess(Access wrapped) {
        super(wrapped);
    }

    @Override
    public boolean allows(Permission permission) {
        return switch (permission) {
            case RECORD_GRADES, ISSUE_REPORTS -> true;
            default -> super.allows(permission);
        };
    }

    @Override
    public String label() {
        return wrapped.label() + " + Grading";
    }
}
