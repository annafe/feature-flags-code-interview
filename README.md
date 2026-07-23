# Feature Flags kata — simplified v3

This is a 60-minute exercise about debugging, safely changing legacy code, and
evolving one small rule abstraction.

The service decides whether a named feature is enabled for a user. Feature
rules are ordinary Java objects; there is no configuration language, parser,
database, web API, or framework involved.

Work through [STEPS.md](STEPS.md) in order. Later requirements are represented
by disabled JUnit test classes in the same ordinary test source directory.

The project requires JDK 25. Start with:

```shell
./gradlew test
```

The first test run is expected to have exactly two failures. Treat this as collaborative production
work: read the code and tests, ask questions, make small safe changes, and
explain your trade-offs.
