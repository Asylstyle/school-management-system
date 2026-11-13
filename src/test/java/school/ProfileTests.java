package school;

final class ProfileTests {
    static void run() {
        Person student = new StudentCreator().register(" S1 ", " Amina ");
        Checks.equal(Role.STUDENT, student.role());
        Checks.equal("S1", student.id());
        Checks.equal("Amina", student.name());
        Checks.equal(Role.TEACHER, new TeacherCreator().register("T1", "Dana").role());
        Checks.fails(IllegalArgumentException.class, () -> new StudentCreator().register(" ", "Amina"));
        Checks.fails(IllegalArgumentException.class, () -> new TeacherCreator().register("T1", null));
    }
}
