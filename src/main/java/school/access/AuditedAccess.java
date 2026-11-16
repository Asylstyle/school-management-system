package school.access;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * Adds behaviour instead of permissions: every refused check is reported,
 * while the decision itself stays with the wrapped access.
 */
public final class AuditedAccess extends AccessDecorator {
    private final Consumer<String> auditLog;

    public AuditedAccess(Access wrapped, Consumer<String> auditLog) {
        super(wrapped);
        this.auditLog = Objects.requireNonNull(auditLog);
    }

    @Override
    public boolean allows(Permission permission) {
        boolean allowed = super.allows(permission);
        if (!allowed) {
            auditLog.accept(label() + " was denied " + permission);
        }
        return allowed;
    }
}
