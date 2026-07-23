package com.example.flags;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

@FunctionalInterface
public interface FlagRule {
    boolean isEnabled(String flagKey, UserContext user);

    static FlagRule enabled() {
        return (flagKey, user) -> true;
    }

    static FlagRule disabled() {
        return (flagKey, user) -> false;
    }

    static FlagRule forCountries(Set<String> countryCodes) {
        Set<String> normalized = Set.copyOf(Objects.requireNonNull(countryCodes, "countryCodes"))
                .stream()
                .map(code -> code.toUpperCase(Locale.ROOT))
                .collect(java.util.stream.Collectors.toUnmodifiableSet());
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException("At least one country is required");
        }
        return (flagKey, user) -> user != null
                && normalized.contains(user.countryCode().toUpperCase(Locale.ROOT));
    }

    static FlagRule percentage(int percentage) {
        throw new UnsupportedOperationException("TODO Step 2: deterministic percentage rule");
    }

    static FlagRule anyOf(List<FlagRule> rules) {
        throw new UnsupportedOperationException("TODO Step 3: compose rules with OR");
    }
}
