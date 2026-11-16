# SchoolDesk

A small Java application for managing school people, courses, enrollments and grades. The console menu keeps the workflow easy to follow, while six design patterns separate the responsibilities.

## Run

Install a **JDK 17 or newer** and make sure `java` and `javac` are on your PATH. No external libraries, database or build tool are required.

Windows PowerShell:

```powershell
./build.ps1 -Run
./build.ps1 -Test
./build.ps1 -Demo
```

Linux / macOS:

```sh
sh build.sh run
sh build.sh test
sh build.sh demo
```

The program starts with fictional sample records: three students, two teachers and four courses; two courses are already completed. Use `0` to exit. Changes stay in memory for the current session. Menu option `11` exports the gradebook as a CSV file; it is a report, not a restorable backup.

## Features

- Register students and teachers with unique IDs.
- Create courses with a teacher and optional capacity, grading policy, semester, credits and prerequisites.
- Enroll students, preventing duplicate enrollments, overfilled courses and enrollment without passed prerequisites.
- Record assessments with scores from 0 to 100 and positive weights.
- Calculate weighted letter grades or pass/fail results.
- Complete a course for a student; only completed and passed courses count for prerequisites and credits.
- Issue a student report with results, course status and earned credits.
- Deliver notifications to the inbox of a student (new grade, completed course, issued report) or a teacher (new student in a course).
- Write refused actions to the activity log.
- View an activity log using UTC+05:00.
- Switch between viewer, registrar, grading and combined administrator permissions.
- Export grades to CSV with escaped fields and spreadsheet formula protection.

IDs and course codes are stored in upper case, and text commands ignore letter case, so `s001` and `S001` mean the same student.

Access modes demonstrate permission composition. They are not user authentication: anyone running this local demo can change the mode. The grading role is school-wide, not restricted to an assigned teacher.

## Design

| Pattern | Classes | Responsibility | Module owner |
| --- | --- | --- | --- |
| Factory Method | `PersonCreator`, `StudentCreator`, `TeacherCreator`, `PersonRegistry` | Create the correct person subtype; the registry keeps one creator per role | adilzhandev |
| Decorator | `Access`, `RegistrarAccess`, `GradingAccess`, `AuditedAccess`, `AccessModes` | Add permissions to a basic viewer and audit refused checks | adilzhandev |
| Builder | `Course.Builder` | Construct a validated, immutable course with optional values | Nurlan-byte |
| Strategy | `GradingStrategy`, `WeightedGrading`, `PassFailGrading`, `GradingPolicies` | Choose a course's grading policy | Nurlan-byte |
| Observer | `EventBus`, `SchoolListener`, `ActivityLog`, `StudentNotifier`, `TeacherNotifier`, `Inbox` | Deliver successful operation events to listeners | Asylstyle |
| Facade | `SchoolFacade` over `PersonRegistry`, `CourseCatalog`, `EnrollmentService`, `GradingService` | Check permissions, coordinate the services and publish events | Asylstyle |

The console calls the facade. The facade checks the decorated permission, then gives the work to four services: the person registry uses creators, the course catalog keeps courses built with the builder, the enrollment service keeps enrollments, and the grading service uses the strategy stored on each course. The console wraps the current access mode in `AuditedAccess`, so refused checks reach the activity log through the facade. Successful operations publish events to the activity log and to the notifiers.

## Grading

The weighted score is `sum(score * weight) / sum(weight)`. Weights are relative and do not need to total 100. An ungraded enrollment displays **Not graded**, rather than zero. The score is rounded to one decimal place before the label is chosen, so the label always matches the displayed score. Letter boundaries are A >= 90, B >= 80, C >= 70, D >= 60, otherwise F. Pass/fail uses a threshold of 60.

For example, a project scored 94 with weight 60 and a quiz scored 82 with weight 40 produce **89.2 / B**.

## Project layout

```text
src/main/java/school/           Console application and sample data
src/main/java/school/core/      Facade, services, course, enrollment, report, CSV export
src/main/java/school/people/    People, creators and the person registry
src/main/java/school/access/    Permissions, access decorators and access modes
src/main/java/school/grading/   Assessments, grading strategies and their registry
src/main/java/school/events/    Event bus, listeners, notifiers and inbox
src/main/java/school/common/    Shared validation
src/test/java/school/           Dependency-free automated tests
docs/                           UML, requirements and project report
build.ps1                       Windows build, test and run commands
build.sh                        Linux/macOS build, test and run commands
```

The test runner covers validation, permission checks, grading boundaries, course capacity, prerequisites, immutable collection snapshots, observer failures, notifications, the student report, CSV export and console workflows. It throws an error and exits unsuccessfully when a check fails; it does not depend on Java assertions being enabled.

See [requirements](docs/requirements.md), [domain rules](docs/domain.md) and [UML overview](docs/architecture.md).

## Scope

This is a single-process course project. Persistence, sign-in, teacher-specific authorization, editing/deleting records, timetables and attendance are possible extensions. The current version deliberately focuses on complete enrollment and grading workflows.
