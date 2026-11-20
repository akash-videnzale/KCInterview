package com.agenticqa.api.fixtures;

import com.agenticqa.core.configuration.ConfigLoader;
import com.agenticqa.core.logging.LogManager;
import org.slf4j.Logger;
import org.testng.annotations.BeforeMethod;

import java.lang.reflect.Method;

/**
 * TestNG base for API tests (no browser); sets logging context per method.
 *
 * @author Akash Aggarwal
 */
public abstract class ApiBaseTest {

    private static final Logger LOG = LogManager.getLogger(ApiBaseTest.class);

    @BeforeMethod(alwaysRun = true)
    public void bindApiContext(Method method) {
        LogManager.putTestContext(method.getDeclaringClass().getSimpleName() + "." + method.getName());
        LOG.info("API test using apiBaseUrl={}", ConfigLoader.get().apiBaseUrl());
    }
}
