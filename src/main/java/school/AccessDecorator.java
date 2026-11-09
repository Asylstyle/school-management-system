package school;

import java.util.Objects;
public abstract class AccessDecorator implements Access {
    protected final Access wrapped;
    protected AccessDecorator(Access wrapped) { this.wrapped = Objects.requireNonNull(wrapped); }
    @Override public boolean allows(Permission permission) { return wrapped.allows(permission); }
    @Override public String label() { return wrapped.label(); }
}
