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

The program starts with fictional sample records: three students, two teachers and three courses. Use `0` to exit. Changes stay in memory for the current session. Menu option `11` exports the gradebook as a CSV file; it is a report, not a restorable backup.

## Features

- Register students and teachers with unique IDs.
- Create courses with a teacher, capacity and grading policy.
- Enroll students, preventing duplicate enrollments and overfilled courses.
- Record assessments with scores from 0 to 100 and positive weights.
- Calculate weighted letter grades or pass/fail results.
- View an activity log using UTC+05:00.
- Switch between viewer, registrar, grading and combined administrator permissions.
- Export grades to CSV with escaped fields and spreadsheet formula protection.

Access modes demonstrate permission composition. They are not user authentication: anyone running this local demo can change the mode. The grading role is school-wide, not restricted to an assigned teacher.

## Design

| Pattern | Classes | Responsibility | Module owner |
| --- | --- | --- | --- |
| Factory Method | `PersonCreator`, `StudentCreator`, `TeacherCreator` | Create the correct person subtype | adilzhandev |
| Decorator | `Access`, `RegistrarAccess`, `GradingAccess` | Add permissions to a basic viewer | adilzhandev |
| Builder | `Course.Builder` | Construct a validated, immutable course | Nurlan-byte |
| Strategy | `GradingStrategy`, `WeightedGrading`, `PassFailGrading` | Choose a course's grading policy | Nurlan-byte |
| Observer | `EventBus`, `SchoolListener`, `ActivityLog` | Deliver successful operation events to listeners | Asylstyle |
| Facade | `SchoolFacade` | Coordinate validation, storage, permissions and notifications | Asylstyle |

The console calls the facade. The facade uses factories to register people and accepts courses built with the builder. Every mutation checks decorated permissions. Successful changes publish events to the activity log. Gradebook results use the strategy stored on each course.

## Grading

The weighted score is `sum(score * weight) / sum(weight)`. Weights are relative and do not need to total 100. An ungraded enrollment displays **Not graded**, rather than zero. Letter boundaries are A >= 90, B >= 80, C >= 70, D >= 60, otherwise F. Pass/fail uses a threshold of 60. Labels use the unrounded score; display values are rounded.

For example, a project scored 94 with weight 60 and a quiz scored 82 with weight 40 produce **89.2 / B**.

## Project layout

```text
src/main/java/school/   Application and pattern implementations
src/test/java/school/   Dependency-free automated tests
docs/                  UML, requirements and project report
build.ps1              Windows build, test and run commands
build.sh               Linux/macOS build, test and run commands
```

The test runner covers validation, permission checks, grading boundaries, course capacity, immutable collection snapshots, observer failures, CSV export and console workflows. It throws an error and exits unsuccessfully when a check fails; it does not depend on Java assertions being enabled.

See [requirements](docs/requirements.md), [domain rules](docs/domain.md) and [UML overview](docs/architecture.md).

## Scope

This is a single-process course project. Persistence, sign-in, teacher-specific authorization, editing/deleting records, timetables and attendance are possible extensions. The current version deliberately focuses on complete enrollment and grading workflows.
