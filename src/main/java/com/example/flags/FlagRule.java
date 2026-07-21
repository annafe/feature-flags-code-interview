package com.example.flags;

/** A compiled feature rule that no longer needs to know the legacy text format. */
@FunctionalInterface
public interface FlagRule {
    boolean isEnabled(String flagKey, UserContext user);
}

