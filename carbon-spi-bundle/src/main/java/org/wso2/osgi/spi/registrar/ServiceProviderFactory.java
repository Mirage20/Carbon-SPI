package org.wso2.osgi.spi.registrar;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory for instantiate service objects.
 * @param <S> Type of the service.
 */
public class ServiceProviderFactory<S> implements ServiceFactory<S> {

    private static final Logger log = LoggerFactory.getLogger(ServiceProviderFactory.class);
    private final Class<S> serviceProviderClass;

    public ServiceProviderFactory(Class<S> clazz) {
        serviceProviderClass = clazz;
    }

    public S getService(Bundle bundle, ServiceRegistration<S> registration) {
        try {
            return serviceProviderClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            log.error("Could not instantiate the service object", e);
        }
        return null;
    }

    public void ungetService(Bundle bundle, ServiceRegistration<S> registration, S service) {

    }


}
