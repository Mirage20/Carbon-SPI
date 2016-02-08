package org.wso2.osgi.spi.processor;

import org.osgi.framework.Bundle;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.wiring.BundleRequirement;
import org.osgi.framework.wiring.BundleWire;
import org.osgi.framework.wiring.BundleWiring;
import org.wso2.osgi.spi.internal.Constants;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

public class ConsumerBundle {

    private Bundle consumerBundle;
    private boolean isVisibilityRestricted;
    private List<BundleRequirement> visibilityRequirements = new ArrayList<>();

    public ConsumerBundle(Bundle consumerBundle) {
        this.consumerBundle = consumerBundle;
        this.isVisibilityRestricted = false;
        this.processVisibilityRequirements();
    }

    public Bundle getConsumerBundle() {
        return consumerBundle;
    }

    private void processVisibilityRequirements() {

        BundleWiring bundleWiring = consumerBundle.adapt(BundleWiring.class);
        if (bundleWiring == null) {
            return;
        }
        visibilityRequirements = bundleWiring.getRequirements(Constants.SERVICELOADER_NAMESPACE);

        if (visibilityRequirements != null && !visibilityRequirements.isEmpty()) {
            isVisibilityRestricted = true;
        }

    }

    public boolean isVisible(String className) {

        if (!isVisibilityRestricted) {
            return true;
        }

        for (BundleRequirement visibilityRequirement : visibilityRequirements) {

            try {
                Filter filter = FrameworkUtil.createFilter(visibilityRequirement.getDirectives().get(Constants.FILTER_DIRECTIVE));
                Dictionary<String, String> lookupVisibility = new Hashtable<>();
                lookupVisibility.put(Constants.SERVICELOADER_NAMESPACE, className);
                if (filter.matchCase(lookupVisibility)) {
                    return true;
                }
            } catch (InvalidSyntaxException e) {
                e.printStackTrace();
            }

        }
        return false;
    }

    public boolean isSingleCardinality(String className) {
        return false; // TODO: 2/7/16 cardinalty fix
    }

    public boolean isVisibilityRestricted() {
        return isVisibilityRestricted;
    }

    public List<BundleRequirement> getVisibilityRequirements() {
        return visibilityRequirements;
    }

    public List<Bundle> getVisibleBundles(){
        BundleWiring bundleWiring = consumerBundle.adapt(BundleWiring.class);
        List<BundleWire> requiredWires = bundleWiring.getRequiredWires(Constants.SERVICELOADER_NAMESPACE);

        List<Bundle> visibleBundles = new ArrayList<>();

        for(BundleWire requiredWire : requiredWires){
            Bundle visibleBundle = requiredWire.getProvider().getBundle();
            if(!visibleBundles.contains(visibleBundle)) {
                visibleBundles.add(requiredWire.getProvider().getBundle());
            }
        }

        return visibleBundles;

    }
}
