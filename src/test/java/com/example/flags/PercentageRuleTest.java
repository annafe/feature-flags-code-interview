package com.example.flags;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Disabled("Enable this class for Step 2")
class PercentageRuleTest {
    @Test
    void supportsZeroAndOneHundredPercent() {
        var user = new UserContext("user-1", "DE");

        assertFalse(FlagRule.percentage(0).isEnabled("checkout", user));
        assertTrue(FlagRule.percentage(100).isEnabled("checkout", user));
        assertFalse(FlagRule.percentage(100).isEnabled("checkout", null));
    }

    @Test
    void isStableForTheSameFlagAndUser() {
        var firstRule = FlagRule.percentage(37);
        var secondRule = FlagRule.percentage(37);
        var user = new UserContext("user-42", "DE");

        assertEquals(firstRule.isEnabled("checkout", user), firstRule.isEnabled("checkout", user));
        assertEquals(firstRule.isEnabled("checkout", user), secondRule.isEnabled("checkout", user));
    }

    @Test
    void includesTheFlagKeyInTheAssignment() {
        var rule = FlagRule.percentage(50);

        long differentAssignments = IntStream.range(0, 1_000)
                .mapToObj(i -> new UserContext("user-" + i, "DE"))
                .filter(user -> rule.isEnabled("checkout", user) != rule.isEnabled("search", user))
                .count();

        assertNotEquals(0, differentAssignments);
    }

    @Test
    void enablesApproximatelyTheRequestedShare() {
        var rule = FlagRule.percentage(20);

        long enabled = IntStream.range(0, 10_000)
                .mapToObj(i -> new UserContext("user-" + i, "DE"))
                .filter(user -> rule.isEnabled("checkout", user))
                .count();

        assertTrue(enabled >= 1_700 && enabled <= 2_300,
                () -> "Expected roughly 20%, but enabled " + enabled);
    }

    @Test
    void rejectsPercentagesOutsideTheRange() {
        assertThrows(IllegalArgumentException.class, () -> FlagRule.percentage(-1));
        assertThrows(IllegalArgumentException.class, () -> FlagRule.percentage(101));
    }
}

