package org.wso2.osgi.spi.internal;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleRequirement;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.util.tracker.BundleTracker;
import org.wso2.osgi.spi.junk.Junk;
import org.wso2.osgi.spi.processor.ConsumerBundle;
import org.wso2.osgi.spi.registrar.ProviderBundle;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

public class ServiceBundleTracker<T> extends BundleTracker<T> {

    private List<ConsumerBundle> consumers = new ArrayList<>();
    private List<ProviderBundle> providers = new ArrayList<>();
    private boolean isTracked = false;

    public ServiceBundleTracker(BundleContext context, int stateMask) {
        super(context, stateMask, null);
    }

    @Override
    public T addingBundle(Bundle bundle, BundleEvent event) {

        isTracked = false;
        findConsumers(bundle);
        findProviders(bundle);

        if (isTracked) {
            System.out.println("TrackerCustom Added: " + bundle.getSymbolicName() + " Event: " + Junk.typeAsString(event));
            return super.addingBundle(bundle, event);
        }

        return null;

    }

    private void findConsumers(Bundle bundle) {

        BundleWiring bundleWiring = bundle.adapt(BundleWiring.class);
        if (bundleWiring == null) {
            return;
        }

        List<BundleRequirement> requirements = bundleWiring.getRequirements(Constants.EXTENDER_CAPABILITY_NAMESPACE);

        for (BundleRequirement requirement : requirements) {

            try {
                Filter filter = FrameworkUtil.createFilter(requirement.getDirectives().get(Constants.FILTER_DIRECTIVE));

                Dictionary<String, String> lookupConsumer = new Hashtable<>();
                lookupConsumer.put(Constants.EXTENDER_CAPABILITY_NAMESPACE, Constants.PROCESSOR_EXTENDER_NAME);
                if (filter.matchCase(lookupConsumer)) {
                    consumers.add(new ConsumerBundle(bundle));
                    isTracked = true;
                    break;
                }

            } catch (InvalidSyntaxException e) {
                e.printStackTrace();
            }
        }
    }

    private void findProviders(Bundle bundle) {

        BundleWiring bundleWiring = bundle.adapt(BundleWiring.class);
        if (bundleWiring == null) {
            return;
        }

        List<BundleCapability> capabilities = bundleWiring.getCapabilities(Constants.SERVICELOADER_NAMESPACE);

        for (BundleCapability capability : capabilities) {
            if (capability.getAttributes().get(Constants.SERVICELOADER_NAMESPACE) != null) {
                providers.add(new ProviderBundle(bundle));
                isTracked = true;
                break;
            }
        }
    }

    public List<ConsumerBundle> getConsumers() {
        return consumers;
    }

    public List<ProviderBundle> getProviders() {
        return providers;
    }

    @Override
    public void modifiedBundle(Bundle bundle, BundleEvent event, T object) {

        super.modifiedBundle(bundle, event, object);
        System.out.println("TrackerCustom Modified: " + bundle.getSymbolicName() + " Event: " + Junk.typeAsString(event));

        if (this.isProvider(bundle)) {
            ProviderBundle providerBundle = this.getProvider(bundle);
            if (event.getType() == BundleEvent.STARTING && providerBundle.requireRegistrar()) {
                providerBundle.registerServices();
            } else if (event.getType() == BundleEvent.STOPPING && providerBundle.requireRegistrar()) {
                providerBundle.unregisterServices();
            }
        }

    }

    @Override
    public void removedBundle(Bundle bundle, BundleEvent event, T object) {
        super.removedBundle(bundle, event, object);
        System.out.println("Tracker Custom Remove: " + bundle.getSymbolicName() + " Event: " + Junk.typeAsString(event));

    }


    public boolean isConsumer(Bundle bundle) {
        for (ConsumerBundle consumerBundle : consumers) {
            if (bundle.equals(consumerBundle.getConsumerBundle())) {
                return true;
            }
        }
        return false;
    }

    public boolean isProvider(Bundle bundle) {
        for (ProviderBundle providerBundle : providers) {
            if (bundle.equals(providerBundle.getProviderBundle())) {
                return true;
            }
        }
        return false;
    }


    public ConsumerBundle getConsumer(Bundle bundle) {
        for (ConsumerBundle consumerBundle : consumers) {
            if (bundle.equals(consumerBundle.getConsumerBundle())) {
                return consumerBundle;
            }
        }
        return null;
    }

    public ProviderBundle getProvider(Bundle bundle) {
        for (ProviderBundle providerBundle : providers) {
            if (bundle.equals(providerBundle.getProviderBundle())) {
                return providerBundle;
            }
        }
        BundleTracker b;

        return null;
    }

    public List<ProviderBundle> getMatchingProviders(String requestingServiceType, ConsumerBundle consumerBundle) {

        List<ProviderBundle> selectedProviders = new ArrayList<>();

        if (consumerBundle.isVisibilityRestricted()) {

            List<BundleRequirement> consumerRequirements = consumerBundle.getVisibilityRequirements();

            for (BundleRequirement consumerRequirement : consumerRequirements) {

                for (ProviderBundle providerBundle : providers) {
                    List<BundleCapability> providerCapabilities = providerBundle.getServiceCapabilities();

                    for (BundleCapability providerCapability : providerCapabilities) {

                        if (consumerRequirement.matches(providerCapability) && !selectedProviders.contains(providerBundle)) {
                            selectedProviders.add(providerBundle);
                        }
                    }
                }

            }
        } else {
            selectedProviders.addAll(providers);
        }


        return selectedProviders;
    }


}
