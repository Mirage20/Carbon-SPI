package org.wso2.osgi.spi;

import org.osgi.framework.BundleContext;
import org.osgi.framework.hooks.weaving.WeavingHook;
import org.wso2.carbon.launcher.FrameworkStartupHook;
import org.wso2.osgi.spi.processor.ConsumerProcessor;

public class WeavingServiceHook implements FrameworkStartupHook {
    @Override
    public void systemBundleStarted(BundleContext systemBundleContext) {
        ConsumerProcessor consumerProcessor = new ConsumerProcessor();
        systemBundleContext.registerService(WeavingHook.class, consumerProcessor, null);

    }
}
