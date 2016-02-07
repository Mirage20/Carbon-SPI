package org.wso2.osgi.spi.internal;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.hooks.weaving.WeavingHook;
import org.osgi.util.tracker.BundleTracker;
import org.wso2.osgi.spi.processor.ConsumerProcessor;

public class ServiceLoaderActivator implements BundleActivator {

    private static ServiceLoaderActivator instance = null;

    private ServiceBundleTracker serviceBundleTracker = null;
    private ServiceRegistration weavingHookService = null;
    private long bundleId;

    public void start(BundleContext context) throws Exception {

        System.out.println("Bundle Activator Start");
        instance = this;
        bundleId = context.getBundle().getBundleId();

        int trackStates = Bundle.STARTING | Bundle.STOPPING | Bundle.RESOLVED | Bundle.INSTALLED | Bundle.UNINSTALLED |Bundle.ACTIVE;
        serviceBundleTracker = new ServiceBundleTracker(context, trackStates);
        serviceBundleTracker.open();
        BundleTracker b;

        ConsumerProcessor consumerProcessor = new ConsumerProcessor();
        weavingHookService = context.registerService(WeavingHook.class, consumerProcessor, null);

        System.out.println("Bundle Activator Started");
    }

    public void stop(BundleContext context) throws Exception {
        serviceBundleTracker.close();
        weavingHookService.unregister();
        System.out.println("Bundle Activator Stop");
    }

    public static ServiceLoaderActivator getInstance() {
        return instance;
    }

    public ServiceBundleTracker getServiceBundleTracker() {
        return serviceBundleTracker;
    }

    public long getBundleId() {
        return bundleId;
    }
}
