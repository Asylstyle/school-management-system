package school;

public final class GradingAccess extends AccessDecorator {
    public GradingAccess(Access wrapped) { super(wrapped); }
    @Override public boolean allows(Permission p) { return p == Permission.RECORD_GRADES || super.allows(p); }
    @Override public String label() { return wrapped.label() + " + Grading"; }
}
