package org.wso2.osgi.spi.test.integration;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.launch.FrameworkFactory;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

public class OSGiTestListener implements ITestListener {

    Framework framework;

    @Override
    public void onStart(ITestContext iTestContext) {
        try {
            Path osgiSystemBundlePath = Paths.get(System.getProperty(TestConstants.BUILD_DIRECTORY_PROPERTY),
                    "osgi", "org.eclipse.osgi.jar");
            URLClassLoader frameworkClassLoader = new URLClassLoader(new URL[]{osgiSystemBundlePath.toUri().toURL()});
            ServiceLoader<FrameworkFactory> loader = ServiceLoader.load(FrameworkFactory.class, frameworkClassLoader);
            FrameworkFactory frameworkFactory = loader.iterator().next();

            Map<String, String> config = new HashMap<>();
            framework = frameworkFactory.newFramework(config);

            framework.init();
            framework.start();

            BundleContext bundleContext = framework.getBundleContext();
            installInitialBundles(bundleContext);
            iTestContext.setAttribute(TestConstants.BUNDLE_CONTEXT_ATTRIBUTE, bundleContext);
        } catch (MalformedURLException | BundleException ex) {
            throw new RuntimeException();
        }
    }

    private void installInitialBundles(BundleContext bundleContext) throws BundleException {
        List<Bundle> initialBundles = new ArrayList<>();
        Path initialBundlesPath = Paths.get(System.getProperty(TestConstants.BUILD_DIRECTORY_PROPERTY),
                "osgi", "initial");
        initialBundles.add(bundleContext.installBundle(Paths.get(initialBundlesPath.toString(),
                "org.eclipse.osgi.services.jar").toUri().toString()));
        initialBundles.add(bundleContext.installBundle(Paths.get(initialBundlesPath.toString(),
                "pax-logging-api.jar").toUri().toString()));
        initialBundles.add(bundleContext.installBundle(Paths.get(initialBundlesPath.toString(),
                "pax-logging-log4j2.jar").toUri().toString()));
        initialBundles.add(bundleContext.installBundle(Paths.get(initialBundlesPath.toString(),
                "org.eclipse.equinox.cm.jar").toUri().toString()));
        initialBundles.add(bundleContext.installBundle(Paths.get(initialBundlesPath.toString(),
                "org.eclipse.equinox.simpleconfigurator.jar").toUri().toString()));

        for (Bundle initialBundle : initialBundles) {
            initialBundle.start();
        }

    }

    @Override
    public void onFinish(ITestContext iTestContext) {
        if (framework != null) {
            try {
                framework.stop();
            } catch (BundleException ignore) {
            }
        }
    }

    @Override
    public void onTestStart(ITestResult iTestResult) {

    }

    @Override
    public void onTestSuccess(ITestResult iTestResult) {

    }

    @Override
    public void onTestFailure(ITestResult iTestResult) {

    }

    @Override
    public void onTestSkipped(ITestResult iTestResult) {

    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult iTestResult) {

    }
}
