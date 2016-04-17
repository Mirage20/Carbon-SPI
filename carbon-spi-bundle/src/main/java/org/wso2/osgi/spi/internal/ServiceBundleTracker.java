package org.wso2.osgi.spi.internal;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleRequirement;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.util.tracker.BundleTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.osgi.spi.registrar.ServiceRegistrar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class tracks the required bundles for the mediator.
 *
 * @param <T> Type of the tracking object
 */
public class ServiceBundleTracker<T> extends BundleTracker<T> {

    private static final Logger log = LoggerFactory.getLogger(ServiceBundleTracker.class);
    private List<ConsumerBundle> consumers = new ArrayList<>();
    private List<ProviderBundle> providers = new ArrayList<>();

    private final BundleCapability mediatorProcessorCapability;
    private final BundleCapability mediatorRegistrarCapability;
    private boolean isTracked = false;

    public ServiceBundleTracker(BundleContext context, int stateMask) {

        super(context, stateMask, null);
        BundleWiring mediatorBundleWiring = context.getBundle().adapt(BundleWiring.class);

        List<BundleCapability> mediatorCapabilities = mediatorBundleWiring.
                getCapabilities(Constants.EXTENDER_CAPABILITY_NAMESPACE);

        BundleCapability processorCapability = null;
        BundleCapability registrarCapability = null;

        for (BundleCapability mediatorCapability : mediatorCapabilities) {

            if (mediatorCapability.getAttributes().containsKey(Constants.EXTENDER_CAPABILITY_NAMESPACE)) {
                String extenderCapabilityType = mediatorCapability.getAttributes()
                        .get(Constants.EXTENDER_CAPABILITY_NAMESPACE).toString();

                if (extenderCapabilityType.equals(Constants.PROCESSOR_EXTENDER_NAME)) {
                    processorCapability = mediatorCapability;
                } else if (extenderCapabilityType.equals(Constants.REGISTRAR_EXTENDER_NAME)) {
                    registrarCapability = mediatorCapability;
                }
            }
        }
        // TODO: 2/8/16 throw runtime exception if null capability found
        this.mediatorProcessorCapability = processorCapability;
        this.mediatorRegistrarCapability = registrarCapability;
    }

    @Override
    public T addingBundle(Bundle bundle, BundleEvent event) {

        isTracked = false;
        findConsumers(bundle);
        findProviders(bundle);

        if (isTracked) {
            if (log.isDebugEnabled()) {
                log.debug("Added bundle: " + bundle.getSymbolicName() + " to the ServiceBundleTracker ");
            }
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
            if (requirement.matches(mediatorProcessorCapability)) {
                consumers.add(new ConsumerBundle(bundle));
                isTracked = true;
                break;
            }
        }
    }

    private void findProviders(Bundle bundle) {

        BundleWiring bundleWiring = bundle.adapt(BundleWiring.class);
        if (bundleWiring == null) {
            return;
        }

        List<BundleCapability> capabilities = bundleWiring.getCapabilities(Constants.SERVICELOADER_NAMESPACE);
        boolean requireRegistrar = false;
        boolean isProvider = false;

        if (!capabilities.isEmpty()) {
            isProvider = true;
        }

        List<BundleRequirement> requirements = bundleWiring.getRequirements(Constants.EXTENDER_CAPABILITY_NAMESPACE);
        for (BundleRequirement requirement : requirements) {
            if (requirement.matches(mediatorRegistrarCapability)) {
                requireRegistrar = true;
                break;
            }
        }

        if (isProvider) {
            try {
                providers.add(new ProviderBundle(bundle, requireRegistrar));
                isTracked = true;
            } catch (IOException e) {
                log.error("Could not track the service provider bundle: " + bundle.getSymbolicName(), e);
            }
        }
    }

    @Override
    public void modifiedBundle(Bundle bundle, BundleEvent event, T object) {

        super.modifiedBundle(bundle, event, object);

        if (this.isProvider(bundle)) {
            ProviderBundle providerBundle = this.getProvider(bundle);
            if (event.getType() == BundleEvent.STARTING && providerBundle.requireRegistrar()) {
                ServiceRegistrar.register(providerBundle);
            } else if (event.getType() == BundleEvent.STOPPING && providerBundle.requireRegistrar()) {
                ServiceRegistrar.unregister(providerBundle);
            }
        }

    }

    @Override
    public void removedBundle(Bundle bundle, BundleEvent event, T object) {
        super.removedBundle(bundle, event, object);
        if (log.isDebugEnabled()) {
            log.debug("Removed bundle: " + bundle.getSymbolicName() + " from the ServiceBundleTracker ");
        }
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
        return null;
    }

    /**
     * Get the matching service provider bundles for the consumer.
     *
     * @param consumerBundle The consumer bundle that requires a service.
     * @return List of matching provider bundles
     */
    public List<ProviderBundle> getMatchingProviders(ConsumerBundle consumerBundle) {

        List<ProviderBundle> selectedProviders = new ArrayList<>();
        if (ServiceLoaderActivator.getInstance().isActive()) {
            if (consumerBundle.isVisibilityRestricted()) {

                List<Bundle> visibleBundles = consumerBundle.getVisibleBundles();

                selectedProviders.addAll(visibleBundles.stream()
                        .filter(bundle -> bundle.getState() == Bundle.ACTIVE && isProvider(bundle))
                        .map(this::getProvider).collect(Collectors.toList()));

            } else {
                selectedProviders.addAll(providers.stream()
                        .filter((providerBundle) -> providerBundle.getProviderBundle().getState() == Bundle.ACTIVE)
                        .collect(Collectors.toList()));
            }

        }
        return selectedProviders;
    }


}
