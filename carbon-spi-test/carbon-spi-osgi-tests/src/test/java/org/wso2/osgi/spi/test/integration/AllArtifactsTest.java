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

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class AllArtifactsTest {

    List<Bundle> allBundles = new ArrayList<>();

    @BeforeClass
    public void setup(ITestContext iTestContext) throws BundleException {
        Object obj = iTestContext.getAttribute(TestConstants.BUNDLE_CONTEXT_ATTRIBUTE);
        if (obj instanceof BundleContext) {
            BundleContext bundleContext = (BundleContext) obj;
            ConsumerProcessor consumerProcessor = new ConsumerProcessor();
            bundleContext.registerService(WeavingHook.class, consumerProcessor, null);

            Path testBundlesPath = Paths.get(System.getProperty(TestConstants.BUILD_DIRECTORY_PROPERTY),
                    "osgi", "test-artifacts");
            allBundles.add(bundleContext.installBundle(Paths.get(testBundlesPath.toString(),
                    "carbon-spi-bundle.jar").toUri().toString()));
            allBundles.add(bundleContext.installBundle(Paths.get(testBundlesPath.toString(),
                    "service-provider-api.jar").toUri().toString()));

            allBundles.add(bundleContext.installBundle(Paths.get(testBundlesPath.toString(),
                    "service-provider-1.jar").toUri().toString()));
            allBundles.add(bundleContext.installBundle(Paths.get(testBundlesPath.toString(),
                    "service-provider-2.jar").toUri().toString()));
            allBundles.add(bundleContext.installBundle(Paths.get(testBundlesPath.toString(),
                    "service-provider-3.jar").toUri().toString()));
            allBundles.add(bundleContext.installBundle(Paths.get(testBundlesPath.toString(),
                    "service-provider-4.jar").toUri().toString()));
            allBundles.add(bundleContext.installBundle(Paths.get(testBundlesPath.toString(),
                    "service-provider-5.jar").toUri().toString()));

            allBundles.add(bundleContext.installBundle(Paths.get(testBundlesPath.toString(),
                    "service-consumer-1.jar").toUri().toString()));
            allBundles.add(bundleContext.installBundle(Paths.get(testBundlesPath.toString(),
                    "service-consumer-2.jar").toUri().toString()));
            allBundles.add(bundleContext.installBundle(Paths.get(testBundlesPath.toString(),
                    "service-consumer-3.jar").toUri().toString()));
            allBundles.add(bundleContext.installBundle(Paths.get(testBundlesPath.toString(),
                    "service-consumer-4.jar").toUri().toString()));
            allBundles.add(bundleContext.installBundle(Paths.get(testBundlesPath.toString(),
                    "service-consumer-5.jar").toUri().toString()));

        }
    }

    @Test
    public void testBundleStart() throws BundleException {
        for (Bundle bundle : allBundles) {
            bundle.start();
            Assert.assertEquals(bundle.getState(), Bundle.ACTIVE, "Test if the the bundle is in Active state");
        }
    }
}
