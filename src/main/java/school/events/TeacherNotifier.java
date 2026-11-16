package school.events;

import java.util.Objects;

public final class TeacherNotifier implements SchoolListener {
    private final Inbox inbox;

    public TeacherNotifier(Inbox inbox) {
        this.inbox = Objects.requireNonNull(inbox);
    }

    @Override
    public void onEvent(SchoolEvent event) {
        if (event.type() == EventType.STUDENT_ENROLLED && event.teacherId() != null) {
            inbox.deliver(event.teacherId(), "New student: " + event.message());
        }
    }
}
