# Candidate steps

The time boxes are guidance. Work in order and keep the design small.

## Step 1 — repair existing behavior (0–15 minutes)

Run:

```shell
./gradlew test
```

The active tests should report exactly two failures. Diagnose and repair the
legacy implementation without weakening or deleting tests.

Required behavior:

- A configured rule decides whether its flag is enabled.
- Country rules may target several countries.
- Unknown flags are disabled.
- `replaceRules` replaces the complete previous rule set; it does not merge.

Keep `FeatureFlagService.isEnabled` usable by existing callers.

## Step 2 — deterministic percentage rule (15–35 minutes)

Open `PercentageRuleTest` and remove its class-level `@Disabled` annotation.
Then run:

```shell
./gradlew test --tests '*PercentageRuleTest'
```

Implement `FlagRule.percentage(int percentage)`.

### What “deterministic percentage” means

A 20% rollout must not choose a fresh random 20% on every request. The same
user evaluating the same flag must always receive the same answer—even in a new
service instance or after an application restart.

One simple mental model is 100 numbered buckets:

1. Combine the `flagKey` and `userId` into one stable identity.
2. Produce a stable integer hash from that identity.
3. Normalize it into a bucket from `0` through `99`. Remember that Java hashes
   can be negative.
4. For 20%, enable buckets `0` through `19`. For 0%, enable none; for 100%,
   enable all non-null users.

This should be approximately 20% across a large population, not necessarily
exactly 20 users in every particular group of 100. Include the flag key so a
user is not automatically in the same rollout bucket for every feature.

Do not use `Random`, current time, or mutable counters. An anonymous (`null`)
user is disabled. Percentages outside `0..100` are invalid.

Add one focused edge-case test of your own.

## Step 3 — compose rules with OR (35–55 minutes)

Open `AnyOfRuleTest` and remove its class-level `@Disabled` annotation. Run:

```shell
./gradlew test --tests '*AnyOfRuleTest'
```

Implement `FlagRule.anyOf(List<FlagRule> rules)`.

- It is enabled when any child rule is enabled.
- Stop evaluating after the first match.
- Reject an empty rule list.
- Defensively copy the supplied list so later caller changes do not alter the
  rule.
- Reuse existing rules; do not introduce a parser or expression language.

## Finish — review (55–60 minutes)

Run the full suite and review the code:

```shell
./gradlew test
```

Explain how you might make live rule replacement safe for concurrent request
threads. This is discussion only, not another implementation requirement.
