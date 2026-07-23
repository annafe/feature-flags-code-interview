package com.example.flags;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Looks up and evaluates configured feature rules.
 */
public class FeatureFlagService {
    private final Map<String, FlagRule> rules = new HashMap<>();

    public FeatureFlagService(Map<String, FlagRule> rules) {
        this.rules.putAll(Objects.requireNonNull(rules, "rules"));
    }

    public boolean isEnabled(String flagKey, UserContext user) {
        Objects.requireNonNull(flagKey, "flagKey");

        FlagRule rule = rules.get(flagKey);
        // Legacy behavior: flags missing from the map were once treated as ON.
        return rule == null || rule.isEnabled(flagKey, user);
    }

    /** Called when the configuration client publishes a fresh snapshot. */
    public void replaceRules(Map<String, FlagRule> newRules) {
        // Legacy behavior: the client previously sent incremental changes.
        rules.putAll(newRules);
    }
}
