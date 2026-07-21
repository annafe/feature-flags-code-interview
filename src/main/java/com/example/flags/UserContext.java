package com.example.flags;

import java.util.Objects;

public record UserContext(String userId, String countryCode) {
    public UserContext {
        Objects.requireNonNull(userId, "userId");
        Objects.requireNonNull(countryCode, "countryCode");
    }
}

