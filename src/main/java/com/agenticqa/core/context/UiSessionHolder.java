package com.agenticqa.core.context;

/**
 * Thread-local holder for the current UI test session.
 * Populated by UiBaseTest lifecycle; cleaned after every method.
 */
public final class UiSessionHolder {

    private static final ThreadLocal<TestSession> CURRENT = new ThreadLocal<>();

    private UiSessionHolder() {
    }

    public static void set(TestSession session) {
        CURRENT.set(session);
    }

    public static TestSession get() {
        TestSession session = CURRENT.get();
        if (session == null) {
            throw new IllegalStateException("No UI TestSession bound to current thread");
        }
        return session;
    }

    public static boolean isBound() {
        return CURRENT.get() != null;
    }

    public static void clear() {
        CURRENT.remove();
    }
}
