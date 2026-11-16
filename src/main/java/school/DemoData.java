package school;

import school.access.Access;
import school.access.AccessModes;
import school.core.Course;
import school.core.SchoolFacade;
import school.grading.Assessment;
import school.grading.PassFailGrading;
import school.people.Role;

public final class DemoData {
    private DemoData() {
    }

    public static void load(SchoolFacade school) {
        Access admin = AccessModes.admin();

        school.register(admin, Role.TEACHER, "T001", "Dana Karimova");
        school.register(admin, Role.TEACHER, "T002", "Arman Sadykov");
        school.register(admin, Role.STUDENT, "S001", "Amina Bekova");
        school.register(admin, Role.STUDENT, "S002", "Dias Alimov");
        school.register(admin, Role.STUDENT, "S003", "Sara Omarova");

        school.addCourse(admin, Course.builder("CS101", "Computer Science")
                .teacher("T001")
                .capacity(20)
                .build());
        school.addCourse(admin, Course.builder("MAT101", "Applied Mathematics")
                .teacher("T002")
                .capacity(24)
                .build());
        school.addCourse(admin, Course.builder("ART101", "Creative Workshop")
                .teacher("T001")
                .capacity(12)
                .credits(3)
                .grading(new PassFailGrading())
                .build());
        school.addCourse(admin, Course.builder("CS201", "Algorithms")
                .teacher("T002")
                .capacity(15)
                .semester("Spring")
                .credits(6)
                .prerequisite("CS101")
                .build());

        school.enroll(admin, "S001", "CS101");
        school.enroll(admin, "S002", "CS101");
        school.enroll(admin, "S003", "ART101");
        school.enroll(admin, "S001", "MAT101");

        school.recordGrade(admin, "S001", "CS101", new Assessment("Project", 94, 60));
        school.recordGrade(admin, "S001", "CS101", new Assessment("Quiz", 82, 40));
        school.recordGrade(admin, "S002", "CS101", new Assessment("Project", 85, 60));
        school.recordGrade(admin, "S003", "ART101", new Assessment("Portfolio", 91, 100));
    }
}
