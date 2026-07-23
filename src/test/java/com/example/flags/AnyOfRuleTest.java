package com.example.flags;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Disabled("Enable this class for Step 3")
class AnyOfRuleTest {
    private final UserContext user = new UserContext("user-1", "DE");

    @Test
    void matchesWhenAnyChildRuleMatches() {
        assertTrue(FlagRule.anyOf(List.of(FlagRule.disabled(), FlagRule.enabled()))
                .isEnabled("checkout", user));
        assertFalse(FlagRule.anyOf(List.of(FlagRule.disabled(), FlagRule.disabled()))
                .isEnabled("checkout", user));
    }

    @Test
    void stopsAfterTheFirstMatch() {
        var evaluations = new AtomicInteger();
        FlagRule shouldNotRun = (flagKey, context) -> {
            evaluations.incrementAndGet();
            return false;
        };

        assertTrue(FlagRule.anyOf(List.of(FlagRule.enabled(), shouldNotRun))
                .isEnabled("checkout", user));
        assertTrue(evaluations.get() == 0);
    }

    @Test
    void rejectsAnEmptyRuleList() {
        assertThrows(IllegalArgumentException.class, () -> FlagRule.anyOf(List.of()));
    }

    @Test
    void defensivelyCopiesTheRuleList() {
        var source = new ArrayList<FlagRule>();
        source.add(FlagRule.disabled());
        FlagRule combined = FlagRule.anyOf(source);

        source.add(FlagRule.enabled());

        assertFalse(combined.isEnabled("checkout", user));
    }
}
