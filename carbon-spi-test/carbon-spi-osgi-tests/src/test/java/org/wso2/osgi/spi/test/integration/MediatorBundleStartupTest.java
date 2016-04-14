package org.wso2.osgi.spi.test.integration;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.nio.file.Paths;

public class MediatorBundleStartupTest {

    Bundle mediatorBundle;

    @BeforeClass
    public void setup(ITestContext iTestContext) throws BundleException {
        Object obj = iTestContext.getAttribute(TestConstants.BUNDLE_CONTEXT_ATTRIBUTE);
        if (obj instanceof BundleContext) {
            BundleContext bundleContext = (BundleContext) obj;
            mediatorBundle = bundleContext.installBundle(
                    Paths.get(System.getProperty(TestConstants.BUILD_DIRECTORY_PROPERTY), "osgi", "test-artifacts",
                            "carbon-spi-bundle.jar").toUri().toString());
        }
    }

    @Test
    public void testBundleStart() throws BundleException {
        mediatorBundle.start();
        Assert.assertEquals(mediatorBundle.getState(),Bundle.ACTIVE,"Test if the mediator bundle is in Active state");
    }
}
