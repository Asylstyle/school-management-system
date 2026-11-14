# Domain rules

## People and permissions

All person IDs share one namespace: a teacher and a student cannot have the same ID. IDs and names are trimmed and must not be blank. IDs and course codes are case-sensitive.

A viewer can read data. Registrar permissions add person registration, course creation and enrollment. Grading permissions add assessment recording. Wrapping a registrar in a grading decorator combines both capabilities without changing the original objects.

## Courses and enrollment

A course has a unique code, title, registered teacher, positive capacity and grading strategy. The builder defaults to 25 seats and weighted letter grading. A course is immutable after construction.

Only registered students can enroll. The pair of student ID and course code identifies an enrollment. The facade rejects duplicate pairs and a course whose capacity is already reached. Read APIs return immutable list snapshots.

## Assessments

Scores must be finite numbers from 0 to 100. Weights must be finite and positive. Assessment titles are unique within one enrollment, ignoring case. A student must be enrolled before receiving a grade. Empty gradebooks have no calculated result.

Both policies use a weighted average. `GradeMath` divides weights by the largest weight before summing. This preserves their proportions and prevents large weights from overflowing the calculation. Policies differ in how they label the score.

## Events

The facade publishes one event after each successful mutation. Rejected operations publish nothing. Duplicate subscriptions are ignored. Unsubscribing removes the listener. A failing listener is reported without preventing other listeners from receiving an event or undoing a completed operation.
