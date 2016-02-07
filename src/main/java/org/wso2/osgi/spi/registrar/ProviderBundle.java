package org.wso2.osgi.spi.registrar;

import org.osgi.framework.Bundle;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleRequirement;
import org.osgi.framework.wiring.BundleWiring;
import org.wso2.osgi.spi.internal.Constants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

// TODO: 2/6/16 handle concurrency
public class ProviderBundle {

    private Bundle providerBundle;
    private List<BundleCapability> serviceCapabilities = new ArrayList<>();
    private Map<String, List<String>> services = new HashMap<>();
    private List<ServiceRegistration> serviceRegistrations = new ArrayList<>();

    public ProviderBundle(Bundle providerBundle) {
        this.providerBundle = providerBundle;
        processProvidedServices();
        processMetaServiceDescriptor();

    }

    public Bundle getProviderBundle() {
        return providerBundle;
    }

    private void processProvidedServices() {

        BundleWiring bundleWiring = providerBundle.adapt(BundleWiring.class);

        if (bundleWiring == null) {
            return;
        }

        List<BundleCapability> capabilities = bundleWiring.getCapabilities(Constants.SERVICELOADER_NAMESPACE);
        for (BundleCapability capability : capabilities) {
            serviceCapabilities.add(capability);
        }

    }

    public boolean hasServiceType(String className) {

        for (BundleCapability serviceCapability : serviceCapabilities) {
            String serviceTypeName = serviceCapability.getAttributes().get(Constants.SERVICELOADER_NAMESPACE).toString();
            if (serviceTypeName.equals(className)) {
                return true;
            }
        }
        return false;
    }

    public boolean requireRegistrar() {

        BundleWiring bundleWiring = providerBundle.adapt(BundleWiring.class);
        if (bundleWiring == null) {
            return false;
        }

        List<BundleRequirement> requirements = bundleWiring.getRequirements(Constants.EXTENDER_CAPABILITY_NAMESPACE);
        for (BundleRequirement requirement : requirements) {
            try {
                Filter filter = FrameworkUtil.createFilter(requirement.getDirectives().get(Constants.FILTER_DIRECTIVE));
                Dictionary<String, String> lookupRegistrar = new Hashtable<>();
                lookupRegistrar.put(Constants.EXTENDER_CAPABILITY_NAMESPACE, Constants.REGISTRAR_EXTENDER_NAME);
                if (filter.matchCase(lookupRegistrar)) {
                    return true;
                }
            } catch (InvalidSyntaxException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private void processMetaServiceDescriptor() {
        List<URL> serviceDescriptors = Collections.list(providerBundle.findEntries(Constants.METAINF_SERVICES, "*", false));

        for (URL serviceDescriptor : serviceDescriptors) {

            try {
                String serviceFilePath = serviceDescriptor.toString();
                String serviceType = serviceFilePath.substring(serviceFilePath.lastIndexOf("/") + 1);
                String providerClassName = null;

                BufferedReader serviceReader = new BufferedReader(new InputStreamReader(serviceDescriptor.openStream()));
                List<String> providerClassNames = new ArrayList<>();
                while ((providerClassName = serviceReader.readLine()) != null) {
                    providerClassName = providerClassName.trim();

                    if (providerClassName.length() == 0)
                        continue; // empty line

                    if (providerClassName.startsWith("#"))
                        continue; // a comment

                    providerClassNames.add(providerClassName);
                }

                services.put(serviceType, providerClassNames);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public void registerServices() {

        if (!requireRegistrar()) {
            return;
        }

        for (BundleCapability serviceCapability : serviceCapabilities) {
            String serviceType = serviceCapability.getAttributes().get(Constants.SERVICELOADER_NAMESPACE).toString();

            if (!services.containsKey(serviceType)) {
                continue;
            }

            List<String> serviceProviders = services.get(serviceType);

            if (serviceCapability.getAttributes().containsKey(Constants.CAPABILITY_REGISTER_DIRECTIVE)) {
                String serviceProvider = serviceCapability.getAttributes().get(Constants.CAPABILITY_REGISTER_DIRECTIVE).toString();

                if (serviceProviders.contains(serviceProvider)) {
                    this.registerServiceProvider(serviceType, serviceProvider);
                } else {
                    // TODO: 2/6/16 throw exception meta inf file invalid
                }

            } else {
                for (String serviceProvider : serviceProviders) {
                    this.registerServiceProvider(serviceType, serviceProvider);
                }
            }
        }
    }

    private void registerServiceProvider(String serviceType, String serviceProvider) {
        try {
            Class<?> clazz = providerBundle.loadClass(serviceProvider);
            ServiceRegistration registration = providerBundle.getBundleContext()
                    // TODO: 2/6/16 register mediator serivce propety insted of null
                    .registerService(serviceType, new ServiceProviderFactory<>(clazz), null);
            serviceRegistrations.add(registration);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void unregisterServices() {

        for (ServiceRegistration registration : serviceRegistrations) {
            registration.unregister();
        }
    }


}
