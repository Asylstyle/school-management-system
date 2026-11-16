package school;

import java.util.ArrayList;
import java.util.List;
import school.access.Access;
import school.access.AccessDeniedException;
import school.access.AccessModes;
import school.access.AuditedAccess;
import school.access.GradingAccess;
import school.access.Permission;
import school.access.ReadOnlyAccess;
import school.access.RegistrarAccess;

final class AccessTests {
    static void run() {
        Access viewer = new ReadOnlyAccess();
        Access registrar = new RegistrarAccess(viewer);
        Access grading = new GradingAccess(viewer);
        Access admin = new GradingAccess(registrar);
        Checks.equal(true, viewer.allows(Permission.VIEW));
        Checks.fails(AccessDeniedException.class, () -> viewer.require(Permission.ENROLL));
        Checks.equal(true, registrar.allows(Permission.MANAGE_PEOPLE));
        Checks.equal(false, registrar.allows(Permission.RECORD_GRADES));
        Checks.equal(true, grading.allows(Permission.RECORD_GRADES));
        Checks.equal(false, grading.allows(Permission.MANAGE_COURSES));
        Checks.equal(true, grading.allows(Permission.ISSUE_REPORTS));
        Checks.equal(false, registrar.allows(Permission.ISSUE_REPORTS));
        for (Permission permission : Permission.values()) {
            Checks.equal(true, admin.allows(permission));
        }
        Checks.equal(false, viewer.allows(Permission.RECORD_GRADES));

        Checks.equal("Viewer", AccessModes.viewer().label());
        Checks.equal("Viewer + Registrar", AccessModes.byName("registrar").label());
        Checks.equal("Viewer + Grading", AccessModes.byName("grading").label());
        Checks.equal("Viewer + Registrar + Grading", AccessModes.admin().label());
        for (Permission permission : Permission.values()) {
            Checks.equal(true, AccessModes.byName("admin").allows(permission));
        }
        Checks.equal("viewer/registrar/grading/admin", AccessModes.names());
        Checks.fails(IllegalArgumentException.class, () -> AccessModes.byName("director"));

        List<String> denied = new ArrayList<>();
        Access audited = new AuditedAccess(registrar, denied::add);
        Checks.equal(true, audited.allows(Permission.ENROLL));
        Checks.equal(false, audited.allows(Permission.RECORD_GRADES));
        Checks.fails(AccessDeniedException.class, () -> audited.require(Permission.ISSUE_REPORTS));
        Checks.equal(List.of("Viewer + Registrar was denied RECORD_GRADES",
                "Viewer + Registrar was denied ISSUE_REPORTS"), denied);
        Checks.equal("Viewer + Registrar", audited.label());
        Checks.fails(NullPointerException.class, () -> new AuditedAccess(viewer, null));
    }
}
