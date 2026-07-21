package com.example.flags;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * Evaluates rules received from an older configuration service.
 */
public class FeatureFlagService {
    private final Map<String, String> configuration;

    public FeatureFlagService(Map<String, String> configuration) {
        this.configuration = new HashMap<>(Objects.requireNonNull(configuration, "configuration"));
    }

    public boolean isEnabled(String flagKey, UserContext user) {
        Objects.requireNonNull(flagKey, "flagKey");

        String configuredRule = configuration.get(flagKey);
        if (configuredRule == null) {
            return false;
        }

        String rule = configuredRule.trim().toUpperCase(Locale.ROOT);
        if (rule.equals("ON")) {
            return true;
        }
        if (rule.equals("OFF")) {
            return false;
        }
        if (rule.startsWith("COUNTRY:")) {
            if (user == null) {
                return false;
            }

            // This old implementation was written when only one country was
            // configured. The upstream format has since started sending lists.
            String configuredCountry = rule.substring("COUNTRY:".length()).trim();
            return configuredCountry.equalsIgnoreCase(user.countryCode());
        }

        throw new IllegalArgumentException("Unsupported feature flag rule: " + configuredRule);
    }

    /** Called when the configuration service publishes a fresh snapshot. */
    public void replaceConfiguration(Map<String, String> newConfiguration) {
        // The old client delivered incremental changes. It now delivers a full
        // snapshot, but this implementation was never revisited.
        configuration.putAll(newConfiguration);
    }
}
