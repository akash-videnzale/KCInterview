package com.agenticqa.core.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.IAlterSuiteListener;
import org.testng.xml.XmlSuite;

import java.util.List;

/**
 * Applies {@code execution.parallelEnabled} / {@code threadCount} from framework config
 * to the TestNG suite before execution. Default remains sequential ({@code parallel=false}).
 *
 * @author Akash Aggarwal
 */
public class ParallelSuiteAlterer implements IAlterSuiteListener {

    private static final Logger LOG = LoggerFactory.getLogger(ParallelSuiteAlterer.class);

    @Override
    public void alter(List<XmlSuite> suites) {
        FrameworkConfig.ExecutionConfig execution = ConfigLoader.get().execution();
        for (XmlSuite suite : suites) {
            if (execution.parallelEnabled()) {
                XmlSuite.ParallelMode mode = resolveMode(execution.mode());
                suite.setParallel(mode);
                suite.setThreadCount(execution.threads());
                LOG.info("Parallel execution ON mode={} threadCount={} (suite={})",
                        mode, execution.threads(), suite.getName());
            } else {
                suite.setParallel(XmlSuite.ParallelMode.NONE);
                suite.setThreadCount(1);
                LOG.info("Parallel execution OFF (suite={})", suite.getName());
            }
        }
    }

    private static XmlSuite.ParallelMode resolveMode(String mode) {
        try {
            return XmlSuite.ParallelMode.getValidParallel(mode);
        } catch (Exception e) {
            LOG.warn("Unknown parallelMode '{}', falling back to methods", mode);
            return XmlSuite.ParallelMode.METHODS;
        }
    }
}
