package school.events;

import java.util.Objects;

public final class StudentNotifier implements SchoolListener {
    private final Inbox inbox;

    public StudentNotifier(Inbox inbox) {
        this.inbox = Objects.requireNonNull(inbox);
    }

    @Override
    public void onEvent(SchoolEvent event) {
        if (event.studentId() == null) {
            return;
        }
        switch (event.type()) {
            case GRADE_RECORDED -> inbox.deliver(event.studentId(), "New grade: " + event.message());
            case COURSE_COMPLETED, REPORT_READY -> inbox.deliver(event.studentId(), event.message());
            default -> {
                // Other events are not addressed to the student.
            }
        }
    }
}
