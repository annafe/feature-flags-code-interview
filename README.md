# Feature Flags kata

This is a 60-minute exercise about debugging, safely changing legacy code, and
evolving a small object-oriented design.

The service decides whether a named feature is enabled for a user. Its
configuration still arrives from a legacy system as strings, and that external
format cannot be changed during this exercise.

Work through [STEPS.md](STEPS.md) in order. Each step gives you a Gradle command
and its business requirement. Do not try to make later test suites pass before
reaching that step.

The project requires JDK 25. Start with:

```shell
./gradlew test
```

The first test run is expected to fail. Treat this as collaborative production
work: read the code and tests, ask questions, make small safe changes, and
explain your trade-offs.

