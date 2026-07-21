package com.example.flags;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ComposableTargetingTest {
    private final FeatureFlagService flags = new FeatureFlagService(Map.of(
            "recommendations", "USER:vip-1,vip-2|COUNTRY:DE,PL|PERCENTAGE:0",
            "global", "OFF|ON"
    ));

    @Test
    void matchesAnExplicitlyTargetedUser() {
        assertTrue(flags.isEnabled("recommendations", new UserContext("vip-2", "FR")));
    }

    @Test
    void matchesAnyOtherRuleSegment() {
        assertTrue(flags.isEnabled("recommendations", new UserContext("ordinary-user", "PL")));
        assertTrue(flags.isEnabled("global", null));
    }

    @Test
    void disablesWhenNoSegmentMatches() {
        assertFalse(flags.isEnabled("recommendations", new UserContext("ordinary-user", "FR")));
    }

    @Test
    void rejectsEmptyOrUnsupportedSegments() {
        var user = new UserContext("user-1", "DE");

        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagService(Map.of("flag", "ON||COUNTRY:DE")).isEnabled("flag", user));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagService(Map.of("flag", "PLAN:PREMIUM|COUNTRY:DE")).isEnabled("flag", user));
    }
}

