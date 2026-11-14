# Architecture

## Pattern relationships

```mermaid
classDiagram
  PersonCreator <|-- StudentCreator
  PersonCreator <|-- TeacherCreator
  Person <|-- Student
  Person <|-- Teacher
  StudentCreator ..> Student : creates
  TeacherCreator ..> Teacher : creates
  Access <|.. ReadOnlyAccess
  Access <|.. AccessDecorator
  AccessDecorator o-- Access : wraps
  AccessDecorator <|-- RegistrarAccess
  AccessDecorator <|-- GradingAccess
  CourseBuilder ..> Course : builds
  Course --> GradingStrategy
  GradingStrategy <|.. WeightedGrading
  GradingStrategy <|.. PassFailGrading
  SchoolFacade --> PersonCreator
  SchoolFacade --> Access
  SchoolFacade --> Course
  SchoolFacade --> SchoolStore
  SchoolFacade --> EventBus
  EventBus o-- SchoolListener
  SchoolListener <|.. ActivityLog
  ConsoleApp --> SchoolFacade
```

`CourseBuilder` represents the nested Java class `Course.Builder`. Store references use IDs rather than duplicating person objects inside enrollments.

## Recording an assessment

```mermaid
sequenceDiagram
  actor User
  participant UI as ConsoleApp
  participant Facade as SchoolFacade
  participant Access
  participant Enrollment
  participant Events as EventBus
  participant Log as ActivityLog
  participant Policy as GradingStrategy
  User->>UI: Record grade
  UI->>Facade: recordGrade(access, student, course, assessment)
  Facade->>Access: require(RECORD_GRADES)
  Facade->>Enrollment: validate enrollment and unique title
  Facade->>Enrollment: add(assessment)
  Facade->>Events: publish(GRADE_RECORDED)
  Events->>Log: onEvent(event)
  UI->>Facade: result(enrollment)
  Facade->>Policy: calculate(assessments)
  Policy-->>UI: score and label through facade
```

All application state belongs to one facade instance. The console is a single-threaded client; the store and event bus are not intended for concurrent server requests.
