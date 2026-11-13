package school;

final class AccessTests {
    static void run() {
        Access viewer = new ReadOnlyAccess();
        Access registrar = new RegistrarAccess(viewer);
        Access grading = new GradingAccess(viewer);
        Access admin = new GradingAccess(registrar);
        Checks.equal(true, viewer.allows(Permission.VIEW));
        Checks.fails(SecurityException.class, () -> viewer.require(Permission.ENROLL));
        Checks.equal(true, registrar.allows(Permission.MANAGE_PEOPLE));
        Checks.equal(false, registrar.allows(Permission.RECORD_GRADES));
        Checks.equal(true, grading.allows(Permission.RECORD_GRADES));
        Checks.equal(false, grading.allows(Permission.MANAGE_COURSES));
        for (Permission p : Permission.values()) Checks.equal(true, admin.allows(p));
        Checks.equal(false, viewer.allows(Permission.RECORD_GRADES));
    }
}
