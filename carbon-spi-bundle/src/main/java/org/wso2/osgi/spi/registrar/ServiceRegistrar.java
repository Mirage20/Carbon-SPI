package org.wso2.osgi.spi.registrar;

import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.wiring.BundleCapability;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.osgi.spi.internal.Constants;
import org.wso2.osgi.spi.internal.ProviderBundle;
import org.wso2.osgi.spi.internal.ServiceLoaderActivator;
import org.wso2.osgi.spi.security.Permissions;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Handles the osgi service registration for the service provider bundles.
 */
public class ServiceRegistrar {
    private static final Logger log = LoggerFactory.getLogger(ServiceRegistrar.class);
    private static Map<ProviderBundle, List<ServiceRegistration>> serviceRegistrations = new ConcurrentHashMap<>();

    /**
     * Register the OSGi service for the bundle if any.
     * @param providerBundle The provider bundle which requires the registration.
     */
    public static void register(ProviderBundle providerBundle) {
        if (!providerBundle.requireRegistrar()) {
            return;
        }

        for (BundleCapability serviceCapability : providerBundle.getServiceCapabilities()) {

            if (!serviceCapability.getAttributes().containsKey(Constants.SERVICELOADER_NAMESPACE)) {
                continue;
            }

            String serviceType = serviceCapability.getAttributes().get(Constants.SERVICELOADER_NAMESPACE).toString();

            if (!providerBundle.getAdvertisedServices().containsKey(serviceType)) {
                continue;
            }

            List<String> advertisedServiceProviders = providerBundle.getAdvertisedServices().get(serviceType);
            final Hashtable<String, ?> serviceProperties = getServiceProperties(serviceCapability);

            if (serviceCapability.getAttributes().containsKey(Constants.CAPABILITY_REGISTER_DIRECTIVE)) {
                String serviceProvider = serviceCapability.getAttributes()
                        .get(Constants.CAPABILITY_REGISTER_DIRECTIVE).toString();

                if (advertisedServiceProviders.contains(serviceProvider)) {
                    registerServiceProvider(providerBundle, serviceType, serviceProvider, serviceProperties);
                } else {
                    // TODO: 2/6/16 throw exception meta inf file invalid or empty register directive
                }

            } else {
                for (String serviceProvider : advertisedServiceProviders) {
                    registerServiceProvider(providerBundle, serviceType, serviceProvider, serviceProperties);
                }
            }
        }
    }

    //Register a service provider with a given service type.
    private static void registerServiceProvider(ProviderBundle providerBundle, String serviceType,
                                                String serviceProvider, Hashtable<String, ?> properties) {

        if (Permissions.canRegisterService(providerBundle, serviceType)) {
            try {
                Class<?> clazz = providerBundle.getServiceProviderClass(serviceProvider);
                ServiceRegistration registration = providerBundle.getBundleContext()
                        .registerService(serviceType, new ServiceProviderFactory<>(clazz), properties);

                if (!serviceRegistrations.containsKey(providerBundle)) {
                    serviceRegistrations.put(providerBundle, new ArrayList<>());
                }
                serviceRegistrations.get(providerBundle).add(registration);
            } catch (ClassNotFoundException e) {
                log.warn("Service provider class not found in "
                        + providerBundle.getProviderBundle().getSymbolicName(), e);
            }
        } else {
            log.warn("Bundle: " + providerBundle.getProviderBundle().getSymbolicName()
                    + " does not have REGISTER permission for the service type: " + serviceType);
        }
    }

    // Get the service properties for the registration.
    private static Hashtable<String, ?> getServiceProperties(BundleCapability serviceCapability) {

        Hashtable<String, Object> serviceProperties = new Hashtable<>();
        Map<String, Object> capabilityAttributes = serviceCapability.getAttributes();

        capabilityAttributes.entrySet().stream()
                .filter((attribute) -> {
                    String key = attribute.getKey();
                    return !(key.startsWith(".") || key.equals(Constants.SERVICELOADER_NAMESPACE)
                            || key.equals(Constants.CAPABILITY_REGISTER_DIRECTIVE));
                })
                .forEach((entry) -> serviceProperties.put(entry.getKey(), entry.getValue()));

        serviceProperties.put(Constants.SERVICELOADER_MEDIATOR_PROPERTY,
                ServiceLoaderActivator.getInstance().getBundleId());

        return serviceProperties;
    }

    /**
     * Unregister the OSGi Services registered for the provider bundle.
     *
     * @param providerBundle The provider bundle for the un-registration of the service
     */
    public static void unregister(ProviderBundle providerBundle) {
        Optional.ofNullable(serviceRegistrations.remove(providerBundle))
                .ifPresent((registrations) -> registrations.forEach(ServiceRegistration::unregister));
    }

    /**
     * Unregister the all OSGi Services registered with the mediator bundle.
     */
    public static void unregisterAll() {

        serviceRegistrations.entrySet().stream().forEach((entry) -> {
            List<ServiceRegistration> registrations = entry.getValue();
            registrations.forEach(ServiceRegistration::unregister);
        });
        serviceRegistrations.clear();
    }
}
