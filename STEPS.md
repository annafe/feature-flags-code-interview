# Candidate steps

The suggested time boxes are guidance. Correctness, reasoning, and a coherent
design matter more than completing every item.

## Step 1 — repair existing behavior (0–15 minutes)

Run:

```shell
./gradlew test
```

Some tests fail because the legacy implementation does not meet its documented
behavior. Diagnose and fix the failures without weakening or deleting tests.

Existing rules:

- `ON` enables a flag for everyone.
- `OFF` disables a flag for everyone.
- `COUNTRY:DE,PL` enables a flag in any listed country.
- An unknown flag is disabled.
- An unsupported rule fails fast.
- `replaceConfiguration` replaces the complete old configuration snapshot; it
  does not merge snapshots.

Preserve the public `isEnabled` entry point.

## Step 2 — deterministic percentage rollout (15–35 minutes)

Run the new requirement tests with the repaired baseline:

```shell
./gradlew test stage2Test
```

Add a rule such as `PERCENTAGE:20`.

- The percentage is an integer from 0 through 100.
- A non-null user must consistently receive the same answer for the same flag,
  including after constructing a new service with the same configuration.
- Assignment must use both the flag key and user ID. Membership in one feature
  must not automatically imply membership in every feature.
- The enabled population should be reasonably close to the requested share.
- An anonymous (`null`) user is disabled for percentage rules.
- Invalid percentages fail fast.

Add at least one focused edge-case test of your own. Refactor the legacy method
if that makes the next change safer; avoid building a general rules platform.

## Step 3 — composable targeting rules (35–55 minutes)

Run all three suites:

```shell
./gradlew test stage2Test stage3Test
```

Product now needs rules that can be composed with **OR**, for example:

```text
USER:user-1,user-2|COUNTRY:DE,PL|PERCENTAGE:10
```

- A flag is enabled when any segment matches.
- `USER` matches an exact user ID from its comma-separated list.
- Existing `ON`, `OFF`, `COUNTRY`, and `PERCENTAGE` segments remain valid.
- Existing single-segment configurations remain valid.
- Empty or unsupported segments fail fast.
- Anonymous users can match only rules that do not require user data, such as
  `ON`.

`FlagRule` and `AnyOfRule` are starter types for the missing OO implementation.
Use, change, or replace them as your design requires, but keep the external
string format at the boundary and preserve `FeatureFlagService.isEnabled`.

## Finish — review (55–60 minutes)

If time remains:

- Make names and responsibilities clear.
- Remove duplication introduced while progressing through the steps.
- Explain what you would do next for thread-safe live configuration refreshes,
  observability, and malformed production configuration.

