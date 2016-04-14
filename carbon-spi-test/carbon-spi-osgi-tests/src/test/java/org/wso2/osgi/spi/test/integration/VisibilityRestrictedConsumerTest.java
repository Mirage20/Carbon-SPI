package org.wso2.osgi.spi.test.integration;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.hooks.weaving.WeavingHook;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.osgi.spi.processor.ConsumerProcessor;

import java.nio.file.Paths;

public class VisibilityRestrictedConsumerTest {

    Bundle consumerBundle;

    @BeforeClass
    public void setup(ITestContext iTestContext) throws BundleException {
        Object obj = iTestContext.getAttribute(TestConstants.BUNDLE_CONTEXT_ATTRIBUTE);
        if (obj instanceof BundleContext) {
            BundleContext bundleContext = (BundleContext) obj;
            ConsumerProcessor consumerProcessor = new ConsumerProcessor();
            bundleContext.registerService(WeavingHook.class, consumerProcessor, null);
            bundleContext.installBundle(Paths.get(System.getProperty(TestConstants.BUILD_DIRECTORY_PROPERTY), "osgi",
                    "test-artifacts", "carbon-spi-bundle.jar").toUri().toString()).start();
            bundleContext.installBundle(Paths.get(System.getProperty(TestConstants.BUILD_DIRECTORY_PROPERTY), "osgi",
                    "test-artifacts", "service-provider-api.jar").toUri().toString()).start();
            bundleContext.installBundle(Paths.get(System.getProperty(TestConstants.BUILD_DIRECTORY_PROPERTY), "osgi",
                    "test-artifacts", "service-provider-2.jar").toUri().toString()).start();
            consumerBundle = bundleContext.installBundle(
                    Paths.get(System.getProperty(TestConstants.BUILD_DIRECTORY_PROPERTY), "osgi", "test-artifacts",
                            "service-consumer-1.jar").toUri().toString());
        }
    }

    @Test
    public void testBundleStart() throws BundleException {
        consumerBundle.start();
        Assert.assertEquals(consumerBundle.getState(), Bundle.ACTIVE, "Test if the consumer bundle is in Active state");
    }
}
