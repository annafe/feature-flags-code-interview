package com.example.flags;

import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PercentageRolloutTest {
    @Test
    void supportsZeroAndOneHundredPercentBoundaries() {
        var flags = new FeatureFlagService(Map.of(
                "none", "PERCENTAGE:0",
                "all", "PERCENTAGE:100"
        ));
        var user = new UserContext("user-1", "DE");

        assertFalse(flags.isEnabled("none", user));
        assertTrue(flags.isEnabled("all", user));
        assertFalse(flags.isEnabled("all", null));
    }

    @Test
    void producesAStableAnswerAcrossServiceInstances() {
        var first = new FeatureFlagService(Map.of("checkout", "PERCENTAGE:37"));
        var second = new FeatureFlagService(Map.of("checkout", "PERCENTAGE:37"));
        var user = new UserContext("user-42", "DE");

        assertEquals(first.isEnabled("checkout", user), second.isEnabled("checkout", user));
    }

    @Test
    void usesTheFlagKeyAsPartOfTheAssignment() {
        var flags = new FeatureFlagService(Map.of(
                "checkout", "PERCENTAGE:50",
                "search", "PERCENTAGE:50"
        ));

        long usersWithDifferentAssignments = IntStream.range(0, 1_000)
                .mapToObj(i -> new UserContext("user-" + i, "DE"))
                .filter(user -> flags.isEnabled("checkout", user) != flags.isEnabled("search", user))
                .count();

        assertNotEquals(0, usersWithDifferentAssignments);
    }

    @Test
    void enablesApproximatelyTheRequestedPopulation() {
        var flags = new FeatureFlagService(Map.of("checkout", "PERCENTAGE:20"));

        long enabled = IntStream.range(0, 10_000)
                .mapToObj(i -> new UserContext("user-" + i, "DE"))
                .filter(user -> flags.isEnabled("checkout", user))
                .count();

        assertTrue(enabled >= 1_700 && enabled <= 2_300,
                () -> "Expected roughly 20%, but enabled " + enabled + " of 10000 users");
    }

    @Test
    void rejectsInvalidPercentages() {
        var user = new UserContext("user-1", "DE");

        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagService(Map.of("flag", "PERCENTAGE:-1")).isEnabled("flag", user));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagService(Map.of("flag", "PERCENTAGE:101")).isEnabled("flag", user));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagService(Map.of("flag", "PERCENTAGE:lots")).isEnabled("flag", user));
    }
}

