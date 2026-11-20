package com.agenticqa.core.reporting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Desktop;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Opens HTML reports in the default browser after a run.
 * Disable with {@code -DOPEN_REPORT=false} or env {@code OPEN_REPORT=false} (useful in CI).
 */
public final class ReportOpener {

    private static final Logger LOG = LoggerFactory.getLogger(ReportOpener.class);
    private static final AtomicBoolean EXTENT_OPENED = new AtomicBoolean(false);

    private ReportOpener() {
    }

    public static boolean isEnabled() {
        String prop = System.getProperty("OPEN_REPORT");
        if (prop == null || prop.isBlank()) {
            prop = System.getenv("OPEN_REPORT");
        }
        if (prop == null || prop.isBlank()) {
            return true;
        }
        return !prop.equalsIgnoreCase("false") && !prop.equals("0") && !prop.equalsIgnoreCase("no");
    }

    /** Opens the stable Extent {@code index.html} at most once per JVM. */
    public static void openExtentOnce() {
        if (!EXTENT_OPENED.compareAndSet(false, true)) {
            return;
        }
        open(ExtentReportManager.latestReportPath());
    }

    public static void open(Path report) {
        if (!isEnabled()) {
            LOG.info("Report auto-open skipped (OPEN_REPORT=false): {}", report);
            return;
        }
        if (report == null || !Files.isRegularFile(report)) {
            LOG.warn("Report auto-open skipped — file missing: {}", report);
            return;
        }
        Path absolute = report.toAbsolutePath().normalize();
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(absolute.toUri());
                LOG.info("Opened report in browser → {}", absolute);
                return;
            }
        } catch (Exception e) {
            LOG.debug("Desktop browse failed, falling back to OS command: {}", e.getMessage());
        }
        openWithOsCommand(absolute);
    }

    private static void openWithOsCommand(Path absolute) {
        String os = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
        List<String> command = new ArrayList<>();
        try {
            if (os.contains("mac")) {
                command.add("open");
                command.add(absolute.toString());
            } else if (os.contains("win")) {
                command.add("cmd");
                command.add("/c");
                command.add("start");
                command.add("");
                command.add(absolute.toString());
            } else {
                command.add("xdg-open");
                command.add(absolute.toString());
            }
            new ProcessBuilder(command).inheritIO().start();
            LOG.info("Opened report via OS command → {}", absolute);
        } catch (Exception e) {
            LOG.warn("Could not open report automatically ({}). Open manually: {}", e.getMessage(), absolute);
        }
    }
}
