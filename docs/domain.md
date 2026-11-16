# Domain rules

## People and permissions

All person IDs share one namespace: a teacher and a student cannot have the same ID. IDs and names are trimmed and must not be blank. IDs and course codes are stored in upper case, so they are not case-sensitive.

`PersonRegistry` keeps one creator for each role and uses it to register a person. A new role needs a new creator and one line in the registry.

A viewer can read data. Registrar permissions add person registration, course creation and enrollment. Grading permissions add assessment recording. Wrapping a registrar in a grading decorator combines both capabilities without changing the original objects. `AccessModes` builds the four ready modes. Every facade method checks a permission: reading methods require `VIEW`. A missing permission throws `AccessDeniedException`.

## Courses and enrollment

A course has a unique code, title, registered teacher, positive capacity, grading strategy, semester, positive number of credits and a list of prerequisite course codes. The builder defaults to 25 seats, weighted letter grading, the semester "Fall", 5 credits and no prerequisites. A course is immutable after construction. Prerequisites must be existing courses.

Only registered students can enroll. The pair of student ID and course code identifies an enrollment. `EnrollmentService` rejects duplicate pairs and a course whose capacity is already reached. The facade rejects an enrollment when the student has not passed a prerequisite course. Read APIs return immutable list snapshots.

## Assessments

Scores must be finite numbers from 0 to 100. Weights must be finite and positive. Assessment titles are unique within one enrollment, ignoring case. A student must be enrolled before receiving a grade. Empty gradebooks have no calculated result.

Both policies use a weighted average. `GradeMath` divides weights by the largest weight before summing. This preserves their proportions and prevents large weights from overflowing the calculation. The average is rounded to one decimal place before a policy labels it. Policies differ in how they label the score. A course is passed when the score is 60 or more.

## Reports

`SchoolFacade.issueReport` returns the courses of one student with their results and the sum of credits of the passed courses. Issuing a report publishes the `REPORT_READY` event.

## Events

The facade publishes one event after each successful operation. An event has a type, a message, a time and the IDs of the student and the teacher it concerns. `ActivityLog` stores every event. `StudentNotifier` reacts to recorded grades and issued reports, `TeacherNotifier` reacts to enrollments. Both write messages to the `Inbox` of the person. Rejected operations publish nothing. Duplicate subscriptions are ignored. Unsubscribing removes the listener. A failing listener is reported without preventing other listeners from receiving an event or undoing a completed operation.
