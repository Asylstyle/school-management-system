package school;

import java.util.Objects;
import java.util.function.Consumer;
public final class StudentNotifier implements SchoolListener {
    private final Consumer<String> channel;
    public StudentNotifier(Consumer<String> channel) { this.channel = Objects.requireNonNull(channel); }
    @Override public void onEvent(SchoolEvent event) {
        if (event.type() == EventType.GRADE_RECORDED) channel.accept("Student notification | New grade " + event.message());
    }
}
