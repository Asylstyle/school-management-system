# Architecture

## Packages

| Package | Content |
| --- | --- |
| `school` | `ConsoleApp`, `ConsoleInput`, `DemoData` |
| `school.core` | `SchoolFacade`, `EnrollmentService`, `GradingService`, `Course`, `Enrollment`, `StudentReport`, `CsvExport` |
| `school.people` | `Person`, `Student`, `Teacher`, `Role`, creators, `PersonRegistry` |
| `school.access` | `Access`, decorators, `AccessModes`, `Permission`, `AccessDeniedException` |
| `school.grading` | `GradingStrategy`, two policies, `GradingPolicies`, `Assessment`, `GradeResult` |
| `school.events` | `EventBus`, `SchoolListener`, `SchoolEvent`, `ActivityLog`, notifiers, `Inbox` |
| `school.common` | `Validation` |

`school.core` depends on the other packages. They do not depend on `school.core`.

## Pattern relationships

```mermaid
classDiagram
  PersonCreator <|-- StudentCreator
  PersonCreator <|-- TeacherCreator
  Person <|-- Student
  Person <|-- Teacher
  StudentCreator ..> Student : creates
  TeacherCreator ..> Teacher : creates
  PersonRegistry o-- PersonCreator
  Access <|.. ReadOnlyAccess
  Access <|.. AccessDecorator
  AccessDecorator o-- Access : wraps
  AccessDecorator <|-- RegistrarAccess
  AccessDecorator <|-- GradingAccess
  AccessModes ..> Access : builds
  CourseBuilder ..> Course : builds
  Course --> GradingStrategy
  GradingStrategy <|.. WeightedGrading
  GradingStrategy <|.. PassFailGrading
  GradingPolicies o-- GradingStrategy
  SchoolFacade *-- PersonRegistry
  SchoolFacade *-- EnrollmentService
  SchoolFacade *-- GradingService
  SchoolFacade --> Access
  SchoolFacade --> EventBus
  EventBus o-- SchoolListener
  SchoolListener <|.. ActivityLog
  SchoolListener <|.. StudentNotifier
  SchoolListener <|.. TeacherNotifier
  StudentNotifier --> Inbox
  TeacherNotifier --> Inbox
  ConsoleApp --> SchoolFacade
```

`CourseBuilder` represents the nested Java class `Course.Builder`. Enrollments keep IDs rather than duplicating person objects.

## Recording an assessment

```mermaid
sequenceDiagram
  actor User
  participant UI as ConsoleApp
  participant Facade as SchoolFacade
  participant Access
  participant Enrollments as EnrollmentService
  participant Grades as GradingService
  participant Events as EventBus
  participant Log as ActivityLog
  participant Notifier as StudentNotifier
  participant Inbox
  User->>UI: Record grade
  UI->>Facade: recordGrade(access, student, course, assessment)
  Facade->>Access: require(RECORD_GRADES)
  Facade->>Enrollments: find(student, course)
  Facade->>Grades: record(enrollment, assessment)
  Facade->>Events: publish(GRADE_RECORDED)
  Events->>Log: onEvent(event)
  Events->>Notifier: onEvent(event)
  Notifier->>Inbox: deliver(studentId, text)
```

All application state belongs to the services of one facade instance. The console is a single-threaded client; the services and the event bus are not intended for concurrent server requests.
