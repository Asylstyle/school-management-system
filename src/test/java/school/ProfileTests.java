package school;

import school.people.Person;
import school.people.PersonRegistry;
import school.people.Role;
import school.people.StudentCreator;
import school.people.TeacherCreator;

final class ProfileTests {
    static void run() {
        Person student = new StudentCreator().register(" s1 ", " Amina ");
        Checks.equal(Role.STUDENT, student.role());
        Checks.equal("S1", student.id());
        Checks.equal("Amina", student.name());
        Checks.equal(Role.TEACHER, new TeacherCreator().register("T1", "Dana").role());
        Checks.fails(IllegalArgumentException.class, () -> new StudentCreator().register(" ", "Amina"));
        Checks.fails(IllegalArgumentException.class, () -> new TeacherCreator().register("T1", null));

        PersonRegistry registry = new PersonRegistry();
        Checks.equal(Role.STUDENT, registry.register(Role.STUDENT, "s1", "Amina").role());
        Checks.equal(Role.TEACHER, registry.register(Role.TEACHER, "t1", "Dana").role());
        Checks.fails(IllegalArgumentException.class, () -> registry.register(Role.TEACHER, "S1", "Same ID"));
        Checks.equal("Amina", registry.find("s1").name());
        Checks.equal("Amina", registry.student("S1").name());
        Checks.equal("Dana", registry.teacher("t1").name());
        Checks.fails(IllegalArgumentException.class, () -> registry.student("T1"));
        Checks.fails(IllegalArgumentException.class, () -> registry.teacher("S1"));
        Checks.fails(IllegalArgumentException.class, () -> registry.find("S9"));
        Checks.equal(2, registry.all().size());
        Checks.fails(UnsupportedOperationException.class, () -> registry.all().clear());
    }
}
