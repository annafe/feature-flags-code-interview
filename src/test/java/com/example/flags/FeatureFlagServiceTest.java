package com.example.flags;

import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FeatureFlagServiceTest {
    private final UserContext germanUser = new UserContext("user-1", "DE");

    @Test
    void evaluatesConfiguredRules() {
        var flags = new FeatureFlagService(Map.of(
                "checkout", FlagRule.enabled(),
                "search", FlagRule.disabled()
        ));

        assertTrue(flags.isEnabled("checkout", germanUser));
        assertFalse(flags.isEnabled("search", germanUser));
    }

    @Test
    void evaluatesCountryRules() {
        var flags = new FeatureFlagService(Map.of(
                "recommendations", FlagRule.forCountries(Set.of("DE", "AT"))
        ));

        assertTrue(flags.isEnabled("recommendations", germanUser));
        assertTrue(flags.isEnabled("recommendations", new UserContext("user-2", "AT")));
        assertFalse(flags.isEnabled("recommendations", new UserContext("user-3", "FR")));
        assertFalse(flags.isEnabled("recommendations", null));
    }

    @Test
    void disablesUnknownFlags() {
        var flags = new FeatureFlagService(Map.of());

        assertFalse(flags.isEnabled("missing", germanUser));
    }

    @Test
    void replacesTheCompleteRuleSnapshot() {
        var flags = new FeatureFlagService(Map.of(
                "checkout", FlagRule.enabled(),
                "search", FlagRule.enabled()
        ));

        flags.replaceRules(Map.of("search", FlagRule.disabled()));

        assertFalse(flags.isEnabled("checkout", germanUser));
        assertFalse(flags.isEnabled("search", germanUser));
    }
}

