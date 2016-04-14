package org.wso2.spi.test.service.consumer.impl;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.wso2.spi.test.service.api.Codec;

import java.util.ServiceLoader;

public class Consumer implements BundleActivator{


    public void start(BundleContext context) throws Exception {
        System.out.println("Bundle Starting Consumer 3");
        ServiceLoader<Codec> ldr = ServiceLoader.load(Codec.class);

        for (Codec spiObject : ldr) {
            spiObject.play("Hello from Consumer 3");
        }
        System.out.println("Bundle Started Consumer 3");
    }

    public void stop(BundleContext context) throws Exception {
        System.out.println("Bundle Stopped Consumer 3");
    }


}
