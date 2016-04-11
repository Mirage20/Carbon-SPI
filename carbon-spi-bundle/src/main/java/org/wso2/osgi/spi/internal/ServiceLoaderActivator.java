package org.wso2.osgi.spi.internal;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.osgi.spi.registrar.ServiceRegistrar;

public class ServiceLoaderActivator implements BundleActivator {

    private static final Logger log = LoggerFactory.getLogger(ServiceLoaderActivator.class);
    private static ServiceLoaderActivator instance = null;

    private ServiceBundleTracker serviceBundleTracker = null;

    private long bundleId;

    public void start(BundleContext context) throws Exception {

        instance = this;
        bundleId = context.getBundle().getBundleId();

        int trackStates = Bundle.STARTING | Bundle.STOPPING | Bundle.RESOLVED | Bundle.ACTIVE;
        serviceBundleTracker = new ServiceBundleTracker(context, trackStates);
        serviceBundleTracker.open();

        log.info("Mediator bundle started");
    }

    public void stop(BundleContext context) throws Exception {
        serviceBundleTracker.close();
        ServiceRegistrar.unregisterAll();
        log.info("Mediator bundle stopped");
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
