package org.wso2.osgi.spi.processor;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleReference;
import org.osgi.framework.wiring.BundleWiring;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.osgi.spi.internal.ConsumerBundle;
import org.wso2.osgi.spi.internal.ProviderBundle;
import org.wso2.osgi.spi.internal.ServiceBundleTracker;
import org.wso2.osgi.spi.internal.ServiceLoaderActivator;
import org.wso2.osgi.spi.security.Permissions;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;

/**
 * Methods of this class are dynamically inject to the classes which use {@link java.util.ServiceLoader} API.
 */
public class DynamicInject {

    private static final Logger log = LoggerFactory.getLogger(DynamicInject.class);
    private static ThreadLocal<ClassLoader> storedClassLoader = new ThreadLocal<>();

    /**
     * Stores the current thread context class loader before calling the {@link DynamicInject#fixContextClassloader}
     */
    public static void storeContextClassloader() {
        AccessController.doPrivileged((PrivilegedAction<Void>) () -> {
            storedClassLoader.set(Thread.currentThread().getContextClassLoader());
            return null;
        });
    }

    /**
     * Restore the saved thread context class loader after calling the {@link java.util.ServiceLoader#load(Class)}.
     */
    public static void restoreContextClassloader() {
        AccessController.doPrivileged((PrivilegedAction<Void>) () -> {
            Thread.currentThread().setContextClassLoader(storedClassLoader.get());
            storedClassLoader.set(null);
            return null;
        });
    }

    /**
     * Find and modify the thread context class loader before calling the {@link java.util.ServiceLoader#load(Class)}.
     * After this modification the {@link java.util.ServiceLoader} can see the service provider classes in other
     * bundles.
     *
     * @param serviceType          The interface or abstract class representing the service.
     * @param consumerBundleLoader The class loader of the consumer bundle which uses the
     *                             {@link java.util.ServiceLoader} API
     */
    public static void fixContextClassloader(Class<?> serviceType, ClassLoader consumerBundleLoader) {

        if (!(consumerBundleLoader instanceof BundleReference)) {
            log.warn(consumerBundleLoader.toString() + ", does not implement "
                    + BundleReference.class.getName());
            return;
        }

        BundleReference consumerBundleReference = ((BundleReference) consumerBundleLoader);

        final ClassLoader contextClassloader = findContextClassloader(consumerBundleReference.getBundle(), serviceType);
        if (contextClassloader != null) {
            AccessController.doPrivileged((PrivilegedAction<Void>) () -> {
                Thread.currentThread().setContextClassLoader(contextClassloader);
                return null;
            });
        }
    }

    /**
     * Find the required thread context class loader for the {@link java.util.ServiceLoader} API.
     *
     * @param consumerBundle Consumer bundle which uses {@link java.util.ServiceLoader} API
     * @param serviceType    The interface or abstract class representing the service.
     * @return A class loader which contains the service provider classes for the serviceType.
     */
    private static ClassLoader findContextClassloader(Bundle consumerBundle, Class<?> serviceType) {

        if (ServiceLoaderActivator.getInstance() == null) {
            return null;
        }
        ConsumerBundle consumer = ServiceLoaderActivator.getInstance().getServiceBundleTracker()
                .getConsumer(consumerBundle);

        if (!Permissions.canConsumeService(consumer, serviceType.getName())) {
            log.warn("Bundle: " + consumer.getConsumerBundle().getSymbolicName() + " does not have GET permission " +
                    "to the service: " + serviceType.getName());
            return null;
        }

        ServiceBundleTracker tracker = ServiceLoaderActivator.getInstance().getServiceBundleTracker();

        List providerBundles = tracker.getMatchingProviders(consumer);

        if (providerBundles.size() == 0) {
            return null;
        } else if (providerBundles.size() == 1) {
            return getProviderBundleClassLoader((ProviderBundle) providerBundles.get(0));
        } else {
            List<ClassLoader> loaders = new ArrayList<>();
            for (Object providerBundle : providerBundles) {
                loaders.add(getProviderBundleClassLoader((ProviderBundle) providerBundle));
            }
            return new CombinedClassLoader(loaders);
        }
    }

    /**
     * Gets the class loader for a service provider bundle.
     *
     * @param providerBundle Service provider bundle.
     * @return the class loader associated with the provider bundle.
     */
    private static ClassLoader getProviderBundleClassLoader(final ProviderBundle providerBundle) {
        return AccessController.doPrivileged((PrivilegedAction<ClassLoader>) ()
                -> providerBundle.getProviderBundle().adapt(BundleWiring.class).getClassLoader());
    }

}
