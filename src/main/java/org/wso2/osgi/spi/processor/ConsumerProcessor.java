package org.wso2.osgi.spi.processor;


import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.osgi.framework.Bundle;
import org.osgi.framework.hooks.weaving.WeavingHook;
import org.osgi.framework.hooks.weaving.WovenClass;
import org.wso2.osgi.spi.internal.ServiceLoaderActivator;

public class ConsumerProcessor implements WeavingHook {


    public void weave(WovenClass wovenClass) {

        Bundle consumerBundle = wovenClass.getBundleWiring().getBundle();
        if (ServiceLoaderActivator.getInstance().getServiceBundleTracker().isConsumer(consumerBundle)) {

            ClassReader classReader = new ClassReader(wovenClass.getBytes());
            ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
            ConsumerClassVisitor consumerClassVisitor = new ConsumerClassVisitor(classWriter, wovenClass.getClassName());
            classReader.accept(consumerClassVisitor, ClassReader.SKIP_FRAMES);
            if (consumerClassVisitor.isModified()) {
                wovenClass.setBytes(classWriter.toByteArray());
                wovenClass.getDynamicImports().add(DynamicInject.class.getPackage().getName());
            }

        }
    }
}
