package com.example.flags;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AnyOfRuleTest {
    private final UserContext user = new UserContext("user-1", "DE");

    @Test
    void matchesWhenAnyChildRuleMatches() {
        FlagRule never = (flagKey, context) -> false;
        FlagRule always = (flagKey, context) -> true;

        assertTrue(new AnyOfRule(List.of(never, always)).isEnabled("checkout", user));
        assertFalse(new AnyOfRule(List.of(never, never)).isEnabled("checkout", user));
    }

    @Test
    void rejectsAnEmptyRuleList() {
        assertThrows(IllegalArgumentException.class, () -> new AnyOfRule(List.of()));
    }
}

