package com.agenticqa.agents.report;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses {@code TestListener} lines from Maven/TestNG console output.
 */
public final class ExecutionResultParser {

    // Prefer Class.method MDC prefix when present (UI/API fixtures set it).
    private static final Pattern RESULT_WITH_CLASS = Pattern.compile(
            "([A-Za-z0-9_]+)\\.([A-Za-z0-9_]+)\\s+TEST (PASS|FAIL|SKIP):\\s*([A-Za-z0-9_]+)"
                    + "(?:\\s+classification=([A-Za-z0-9_]+))?\\s*(?:\\((\\d+)\\s*ms\\))?"
    );

    // Fallback when MDC testName is empty (e.g. some unit tests).
    private static final Pattern RESULT_PLAIN = Pattern.compile(
            "TEST (PASS|FAIL|SKIP):\\s*([A-Za-z0-9_]+)"
                    + "(?:\\s+classification=([A-Za-z0-9_]+))?\\s*(?:\\((\\d+)\\s*ms\\))?"
    );

    private static final Pattern SUITE_FINISH = Pattern.compile(
            "Suite finish:.*?passed=(\\d+)\\s+failed=(\\d+)\\s+skipped=(\\d+)"
    );

    private ExecutionResultParser() {
    }

    public static List<ExecutionTestResult> parse(String log) {
        if (log == null || log.isBlank()) {
            return List.of();
        }
        // Last result for a class.method wins (covers re-runs in the same log).
        Map<String, ExecutionTestResult> byKey = new LinkedHashMap<>();
        for (String line : log.split("\\R")) {
            Matcher withClass = RESULT_WITH_CLASS.matcher(line);
            if (withClass.find()) {
                String className = withClass.group(1);
                String methodFromPrefix = withClass.group(2);
                ExecutionTestResult.Status status = ExecutionTestResult.Status.valueOf(withClass.group(3));
                String methodName = withClass.group(4);
                if (methodName == null || methodName.isBlank()) {
                    methodName = methodFromPrefix;
                }
                String classification = withClass.group(5);
                long duration = withClass.group(6) == null ? 0L : Long.parseLong(withClass.group(6));
                byKey.put(className + "#" + methodName,
                        new ExecutionTestResult(className, methodName, status, duration, classification));
                continue;
            }
            Matcher plain = RESULT_PLAIN.matcher(line);
            if (plain.find()) {
                ExecutionTestResult.Status status = ExecutionTestResult.Status.valueOf(plain.group(1));
                String methodName = plain.group(2);
                String classification = plain.group(3);
                long duration = plain.group(4) == null ? 0L : Long.parseLong(plain.group(4));
                byKey.put("#" + methodName,
                        new ExecutionTestResult("", methodName, status, duration, classification));
            }
        }
        return new ArrayList<>(byKey.values());
    }

    public static SuiteCounts parseSuiteCounts(String log) {
        if (log == null || log.isBlank()) {
            return SuiteCounts.empty();
        }
        Matcher matcher = SUITE_FINISH.matcher(log);
        SuiteCounts last = SuiteCounts.empty();
        while (matcher.find()) {
            last = new SuiteCounts(
                    Integer.parseInt(matcher.group(1)),
                    Integer.parseInt(matcher.group(2)),
                    Integer.parseInt(matcher.group(3))
            );
        }
        return last;
    }

    public record SuiteCounts(int passed, int failed, int skipped) {
        static SuiteCounts empty() {
            return new SuiteCounts(0, 0, 0);
        }

        public int total() {
            return passed + failed + skipped;
        }
    }
}
