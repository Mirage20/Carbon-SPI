package org.wso2.osgi.spi;

import org.osgi.framework.hooks.weaving.WeavingHook;
import org.wso2.carbon.launcher.CarbonServerEvent;
import org.wso2.carbon.launcher.CarbonServerListener;
import org.wso2.osgi.spi.processor.ConsumerProcessor;

/**
 * Class for registering the weaving hook service to the OSGi framework via the system bundle.
 */
public class WeavingServiceHook implements CarbonServerListener {

    /**
     * Receives notification of a CarbonServerEvent.
     *
     * @param event CarbonServerEvent
     */
    @Override
    public void notify(CarbonServerEvent event) {
        if (event.getType() == CarbonServerEvent.BEFORE_LOADING_INITIAL_BUNDLES) {
            ConsumerProcessor consumerProcessor = new ConsumerProcessor();
            event.getSystemBundleContext().registerService(WeavingHook.class, consumerProcessor, null);
        }
    }
}
