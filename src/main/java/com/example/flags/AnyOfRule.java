package com.example.flags;

import java.util.List;

/**
 * Starter for Step 3. It should match when any of its child rules matches.
 * You may change or replace this type if another small design is clearer.
 */
public final class AnyOfRule implements FlagRule {
    private final List<FlagRule> rules;

    public AnyOfRule(List<FlagRule> rules) {
        this.rules = List.copyOf(rules);
    }

    @Override
    public boolean isEnabled(String flagKey, UserContext user) {
        throw new UnsupportedOperationException("TODO: compose child rules");
    }
}

