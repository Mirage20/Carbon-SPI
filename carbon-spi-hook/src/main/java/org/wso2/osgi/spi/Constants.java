package org.wso2.osgi.spi;

/**
 * Constants required for the class processor.
 */
public class Constants {
    //Extender namespace
    public static final String EXTENDER_CAPABILITY_NAMESPACE = "osgi.extender";

    // The names of the extenders involved
    public static final String PROCESSOR_EXTENDER_NAME = "osgi.serviceloader.processor";

    // Type and package of the DynamicInject class
    public static final String DYNAMIC_INJECT_CLASS_NAME = "Lorg/wso2/osgi/spi/processor/DynamicInject;";
    public static final String DYNAMIC_INJECT_PACKAGE_NAME = "org.wso2.osgi.spi.processor";

}
