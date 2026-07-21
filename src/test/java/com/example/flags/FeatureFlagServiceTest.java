package com.example.flags;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FeatureFlagServiceTest {
    private final UserContext germanUser = new UserContext("user-123", "DE");

    @Test
    void evaluatesGloballyEnabledAndDisabledFlags() {
        var flags = new FeatureFlagService(Map.of(
                "new-checkout", "ON",
                "new-search", "OFF"
        ));

        assertTrue(flags.isEnabled("new-checkout", germanUser));
        assertFalse(flags.isEnabled("new-search", germanUser));
    }

    @Test
    void enablesAFlagInASingleConfiguredCountry() {
        var flags = new FeatureFlagService(Map.of("recommendations", "COUNTRY:DE"));

        assertTrue(flags.isEnabled("recommendations", germanUser));
        assertFalse(flags.isEnabled("recommendations", new UserContext("user-456", "FR")));
        assertFalse(flags.isEnabled("recommendations", null));
    }

    @Test
    void enablesAFlagInAnyOfSeveralConfiguredCountries() {
        var flags = new FeatureFlagService(Map.of("recommendations", "COUNTRY:DE, PL"));

        assertTrue(flags.isEnabled("recommendations", germanUser));
        assertTrue(flags.isEnabled("recommendations", new UserContext("user-456", "PL")));
        assertFalse(flags.isEnabled("recommendations", new UserContext("user-789", "FR")));
    }

    @Test
    void disablesUnknownFlags() {
        var flags = new FeatureFlagService(Map.of());

        assertFalse(flags.isEnabled("missing", germanUser));
    }

    @Test
    void failsFastForAnUnsupportedRule() {
        var flags = new FeatureFlagService(Map.of("new-checkout", "SOMEDAY"));

        assertThrows(IllegalArgumentException.class,
                () -> flags.isEnabled("new-checkout", germanUser));
    }

    @Test
    void replacesTheCompleteConfigurationSnapshot() {
        var flags = new FeatureFlagService(Map.of(
                "new-checkout", "ON",
                "recommendations", "ON"
        ));

        flags.replaceConfiguration(Map.of("recommendations", "OFF"));

        assertFalse(flags.isEnabled("new-checkout", germanUser));
        assertFalse(flags.isEnabled("recommendations", germanUser));
    }
}

