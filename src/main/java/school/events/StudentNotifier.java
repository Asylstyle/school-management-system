package school.events;

import java.util.Objects;

public final class StudentNotifier implements SchoolListener {
    private final Inbox inbox;

    public StudentNotifier(Inbox inbox) {
        this.inbox = Objects.requireNonNull(inbox);
    }

    @Override
    public void onEvent(SchoolEvent event) {
        if (event.type() == EventType.GRADE_RECORDED) {
            inbox.deliver(event.studentId(), "New grade: " + event.message());
        }
        if (event.type() == EventType.REPORT_READY) {
            inbox.deliver(event.studentId(), event.message());
        }
    }
}
