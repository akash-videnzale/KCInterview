package com.agenticqa.agents.report;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

/**
 * Builds a compact HTML pass/fail table for AgentCli execution runs.
 */
public final class ExecutionHtmlReportGenerator {

    private ExecutionHtmlReportGenerator() {
    }

    public static String generate(
            String runLabel,
            String groups,
            Integer exitCode,
            List<ExecutionTestResult> results,
            ExecutionResultParser.SuiteCounts suiteCounts
    ) {
        int pass = (int) results.stream().filter(r -> r.status() == ExecutionTestResult.Status.PASS).count();
        int fail = (int) results.stream().filter(r -> r.status() == ExecutionTestResult.Status.FAIL).count();
        int skip = (int) results.stream().filter(r -> r.status() == ExecutionTestResult.Status.SKIP).count();
        if (suiteCounts != null && suiteCounts.total() > 0) {
            pass = suiteCounts.passed();
            fail = suiteCounts.failed();
            skip = suiteCounts.skipped();
        }

        String overall;
        String overallClass;
        if (exitCode != null && exitCode == 0 && fail == 0) {
            overall = "PASS";
            overallClass = "pass";
        } else if (fail > 0 || (exitCode != null && exitCode != 0)) {
            overall = "FAIL";
            overallClass = "fail";
        } else {
            overall = "UNKNOWN";
            overallClass = "skip";
        }

        String stamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        StringBuilder rows = new StringBuilder();
        if (results.isEmpty()) {
            rows.append("""
                    <tr>
                      <td colspan="4" class="empty">No TEST PASS/FAIL lines found in the execution log.</td>
                    </tr>
                    """);
        } else {
            int i = 1;
            for (ExecutionTestResult result : results) {
                String statusClass = result.status().name().toLowerCase();
                String classification = result.classification() == null ? "—" : escape(result.classification());
                String duration = result.durationMs() > 0 ? result.durationMs() + " ms" : "—";
                rows.append("""
                        <tr>
                          <td>%d</td>
                          <td><code>%s</code></td>
                          <td><span class="badge %s">%s</span></td>
                          <td>%s · %s</td>
                        </tr>
                        """.formatted(
                        i++,
                        escape(result.displayName()),
                        statusClass,
                        result.status().name(),
                        escape(duration),
                        classification
                ));
            }
        }

        return """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                  <meta charset="utf-8" />
                  <meta name="viewport" content="width=device-width, initial-scale=1" />
                  <title>AgentCli execution report — %s</title>
                  <style>
                    :root {
                      --bg: #f7f4ef;
                      --ink: #1a1a1a;
                      --muted: #5c5c5c;
                      --card: #ffffff;
                      --line: #e2dcd3;
                      --pass: #0b6e4f;
                      --pass-bg: #e6f3ee;
                      --fail: #a11a2e;
                      --fail-bg: #fdecee;
                      --skip: #8a5a00;
                      --skip-bg: #fff6e5;
                    }
                    * { box-sizing: border-box; }
                    body {
                      margin: 0;
                      font-family: "Segoe UI", "Helvetica Neue", Arial, sans-serif;
                      color: var(--ink);
                      background:
                        radial-gradient(900px 400px at 0%% -10%%, #e8f0ea 0%%, transparent 55%%),
                        var(--bg);
                      line-height: 1.5;
                    }
                    main {
                      max-width: 920px;
                      margin: 0 auto;
                      padding: 2rem 1.25rem 3rem;
                    }
                    h1 { margin: 0 0 0.35rem; font-size: 1.6rem; letter-spacing: -0.02em; }
                    .meta { color: var(--muted); margin: 0 0 1.25rem; font-size: 0.95rem; }
                    .summary {
                      display: flex;
                      flex-wrap: wrap;
                      gap: 0.6rem;
                      margin-bottom: 1.25rem;
                    }
                    .pill {
                      border: 1px solid var(--line);
                      background: var(--card);
                      border-radius: 999px;
                      padding: 0.35rem 0.8rem;
                      font-size: 0.92rem;
                    }
                    .pill.overall.pass { background: var(--pass-bg); border-color: #b7dccb; color: var(--pass); font-weight: 600; }
                    .pill.overall.fail { background: var(--fail-bg); border-color: #f0b7c0; color: var(--fail); font-weight: 600; }
                    .pill.overall.skip { background: var(--skip-bg); border-color: #f0d9a8; color: var(--skip); font-weight: 600; }
                    table {
                      width: 100%%;
                      border-collapse: collapse;
                      background: var(--card);
                      border: 1px solid var(--line);
                      border-radius: 10px;
                      overflow: hidden;
                    }
                    th, td {
                      text-align: left;
                      padding: 0.65rem 0.75rem;
                      border-bottom: 1px solid var(--line);
                      vertical-align: top;
                      font-size: 0.94rem;
                    }
                    th { background: #f3efe9; font-weight: 600; }
                    tr:last-child td { border-bottom: none; }
                    code { font-family: ui-monospace, SFMono-Regular, Menlo, Consolas, monospace; font-size: 0.88em; }
                    .badge {
                      display: inline-block;
                      min-width: 3.4rem;
                      text-align: center;
                      border-radius: 6px;
                      padding: 0.15rem 0.45rem;
                      font-weight: 700;
                      font-size: 0.82rem;
                      letter-spacing: 0.03em;
                    }
                    .badge.pass { background: var(--pass-bg); color: var(--pass); }
                    .badge.fail { background: var(--fail-bg); color: var(--fail); }
                    .badge.skip { background: var(--skip-bg); color: var(--skip); }
                    .empty { color: var(--muted); font-style: italic; }
                    footer { margin-top: 1rem; color: var(--muted); font-size: 0.85rem; }
                  </style>
                </head>
                <body>
                  <main>
                    <h1>AgentCli execution report</h1>
                    <p class="meta">%s · groups=<code>%s</code> · generated %s</p>
                    <div class="summary">
                      <span class="pill overall %s">Overall: %s</span>
                      <span class="pill">Passed: %d</span>
                      <span class="pill">Failed: %d</span>
                      <span class="pill">Skipped: %d</span>
                      <span class="pill">Exit code: %s</span>
                    </div>
                    <table>
                      <thead>
                        <tr>
                          <th>#</th>
                          <th>Test case</th>
                          <th>Result</th>
                          <th>Duration / notes</th>
                        </tr>
                      </thead>
                      <tbody>
                %s
                      </tbody>
                    </table>
                    <footer>
                      Source: Surefire <code>TEST-*.xml</code> when present, else
                      <code>07-execution-log.txt</code> TestListener PASS/FAIL lines.
                      Full Extent report remains at <code>target/extent-report/index.html</code>.
                    </footer>
                  </main>
                </body>
                </html>
                """.formatted(
                escape(runLabel),
                escape(runLabel),
                escape(Objects.toString(groups, "smoke")),
                stamp,
                overallClass,
                overall,
                pass,
                fail,
                skip,
                exitCode == null ? "—" : String.valueOf(exitCode),
                rows
        );
    }

    private static String escape(String value) {
        if (value == null) {
            return "";
        }
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}
