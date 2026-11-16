package school;

import java.time.Instant;
import java.util.ArrayList;
import school.access.AccessDeniedException;
import school.access.AccessModes;
import school.core.SchoolFacade;
import school.events.ActivityLog;
import school.events.EventBus;
import school.events.EventType;
import school.events.Inbox;
import school.events.SchoolEvent;
import school.events.StudentNotifier;
import school.events.TeacherNotifier;
import school.grading.Assessment;

final class ObserverTests {
    static void run() {
        var failures = new ArrayList<RuntimeException>();
        EventBus bus = new EventBus(failures::add);
        ActivityLog log = new ActivityLog();
        bus.subscribe(event -> {
            throw new IllegalStateException("Unavailable listener");
        });
        bus.subscribe(log);
        bus.subscribe(log);
        SchoolEvent created = new SchoolEvent(EventType.COURSE_CREATED, "Created", Instant.EPOCH, "", "T1");
        bus.publish(created);
        Checks.equal(1, log.events().size());
        Checks.equal(1, failures.size());
        Checks.equal(created, log.events().get(0));
        bus.unsubscribe(log);
        bus.publish(created);
        Checks.equal(1, log.events().size());
        Checks.fails(UnsupportedOperationException.class, () -> log.events().clear());

        Inbox inbox = new Inbox();
        bus.subscribe(log);
        bus.subscribe(new StudentNotifier(inbox));
        bus.subscribe(new TeacherNotifier(inbox));
        bus.publish(created);
        Checks.equal(0, inbox.of("T1").size());
        bus.publish(new SchoolEvent(EventType.GRADE_RECORDED, "Quiz: 82.0", Instant.EPOCH, "s1", "T1"));
        bus.publish(new SchoolEvent(EventType.STUDENT_ENROLLED, "Amina enrolled in CS101", Instant.EPOCH, "S1", "T1"));
        bus.publish(new SchoolEvent(EventType.REPORT_READY, "Report issued", Instant.EPOCH, "S1", ""));
        Checks.equal(2, inbox.of("S1").size());
        Checks.equal("New grade: Quiz: 82.0", inbox.of("s1").get(0));
        Checks.equal("Report issued", inbox.of("S1").get(1));
        Checks.equal(1, inbox.of("T1").size());
        Checks.equal("New student: Amina enrolled in CS101", inbox.of("T1").get(0));
        Checks.equal(0, inbox.of("S2").size());
        Checks.equal(5, log.events().size());
        Checks.fails(UnsupportedOperationException.class, () -> inbox.of("S1").clear());
        Checks.fails(NullPointerException.class, () -> new StudentNotifier(null));
        Checks.fails(NullPointerException.class, () -> new TeacherNotifier(null));

        Inbox mail = new Inbox();
        EventBus events = new EventBus();
        ActivityLog activity = new ActivityLog();
        events.subscribe(activity);
        events.subscribe(new StudentNotifier(mail));
        events.subscribe(new TeacherNotifier(mail));
        SchoolFacade school = new SchoolFacade(events);
        DemoData.load(school);
        Checks.equal(2, mail.of("S001").size());
        Checks.equal(1, mail.of("S002").size());
        Checks.equal(3, mail.of("T001").size());
        Checks.equal(1, mail.of("T002").size());

        int before = activity.events().size();
        school.recordGrade(AccessModes.admin(), "s002", "cs101", new Assessment("Quiz", 72, 40));
        Checks.equal(before + 1, activity.events().size());
        Checks.equal(2, mail.of("S002").size());
        Checks.equal("New grade: Quiz: 72.0 for Dias Alimov / CS101", mail.of("S002").get(1));
        Checks.fails(AccessDeniedException.class,
                () -> school.recordGrade(AccessModes.viewer(), "S002", "CS101", new Assessment("Final exam", 80, 60)));
        Checks.equal(2, mail.of("S002").size());
        school.issueReport(AccessModes.viewer(), "S002");
        Checks.equal("Report issued for Dias Alimov, credits earned: 5", mail.of("S002").get(2));
    }
}
