package com.blinkit.util;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class LoginRateLimiter {
    private static final Logger LOGGER = Logger.getLogger(LoginRateLimiter.class.getName());
    private static final int MAX_TRACKED_ADDRESSES = 20_000;
    private static final LoginRateLimiter INSTANCE = new LoginRateLimiter();
    private final int maxFailures = configuredInteger("LOGIN_MAX_FAILURES", 5, 3, 20);
    private final long windowMillis = configuredInteger("LOGIN_WINDOW_SECONDS", 300, 30, 3600) * 1000L;
    private final long initialLockMillis = configuredInteger("LOGIN_LOCK_SECONDS", 60, 30, 600) * 1000L;
    private final long maxLockMillis = configuredInteger("LOGIN_MAX_LOCK_SECONDS", 900, 60, 3600) * 1000L;
    private final Map<String, AttemptState> attempts = new LinkedHashMap<>(128, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, AttemptState> eldest) {
            return size() > MAX_TRACKED_ADDRESSES;
        }
    };

    private LoginRateLimiter() {}

    public static LoginRateLimiter shared() {
        return INSTANCE;
    }

    public synchronized int retryAfterSeconds(String remoteAddress) {
        long now = System.currentTimeMillis();
        removeExpired(now);
        AttemptState state = attempts.get(addressKey(remoteAddress));
        if (state == null || state.lockedUntil <= now) return 0;
        return (int) Math.max(1, (state.lockedUntil - now + 999) / 1000);
    }

    public synchronized void recordFailure(String remoteAddress) {
        long now = System.currentTimeMillis();
        String key = addressKey(remoteAddress);
        removeExpired(now);
        AttemptState state = attempts.computeIfAbsent(key, ignored -> new AttemptState(now));
        if (state.lockedUntil > now) return;
        if (now - state.windowStarted >= windowMillis) {
            state.failures = 0;
            state.windowStarted = now;
        }
        state.lastFailure = now;
        state.failures++;
        if (state.failures >= maxFailures) {
            long multiplier = 1L << Math.min(state.lockLevel, 30);
            state.lockedUntil = now + Math.min(maxLockMillis, initialLockMillis * multiplier);
            state.lockLevel++;
            state.failures = 0;
            state.windowStarted = now;
            LOGGER.log(Level.WARNING, "Temporarily rate-limiting repeated authentication failures.");
        }
    }

    public synchronized void recordSuccess(String remoteAddress) {
        attempts.remove(addressKey(remoteAddress));
    }

    private void removeExpired(long now) {
        long expireAfter = windowMillis + maxLockMillis;
        Iterator<Map.Entry<String, AttemptState>> iterator = attempts.entrySet().iterator();
        while (iterator.hasNext()) {
            AttemptState state = iterator.next().getValue();
            if (state.lockedUntil <= now && now - state.lastFailure > expireAfter) iterator.remove();
        }
    }

    private String addressKey(String remoteAddress) {
        return remoteAddress == null || remoteAddress.isBlank()
                ? "unknown"
                : remoteAddress.trim().toLowerCase(Locale.ROOT);
    }

    private static int configuredInteger(String name, int defaultValue, int minimum, int maximum) {
        String value = System.getenv(name);
        if (value == null || value.isBlank()) return defaultValue;
        try {
            int parsed = Integer.parseInt(value);
            if (parsed < minimum || parsed > maximum) {
                throw new IllegalStateException(name + " must be between " + minimum + " and " + maximum + ".");
            }
            return parsed;
        } catch (NumberFormatException e) {
            throw new IllegalStateException(name + " must be an integer between " + minimum + " and " + maximum + ".", e);
        }
    }

    private static final class AttemptState {
        private long windowStarted;
        private long lastFailure;
        private long lockedUntil;
        private int failures;
        private int lockLevel;

        private AttemptState(long now) {
            windowStarted = now;
            lastFailure = now;
        }
    }
}
