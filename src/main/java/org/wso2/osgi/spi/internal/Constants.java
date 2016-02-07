package org.wso2.osgi.spi.internal;

public class Constants {

    public static String PROVIDE_CAPABILITY = "Provide-Capability";
    public static String REQUIRE_CAPABILITY = "Require-Capability";
    public static String EXTENDER_CAPABILITY_NAMESPACE = "osgi.extender";
    public static String FILTER_DIRECTIVE = "filter";

    // These are two proprietary headers which predated the ServiceLoader Mediator
    // specification and are more powerful than what is specified there
    public String SPI_CONSUMER_HEADER = "SPI-Consumer";
    public String SPI_PROVIDER_HEADER = "SPI-Provider";

    // ServiceLoader capability and related directive
    public final static String SERVICELOADER_NAMESPACE = "osgi.serviceloader";
    public static final String CAPABILITY_REGISTER_DIRECTIVE = "register";

    // Service registration property
    public static final String SERVICELOADER_MEDIATOR_PROPERTY = "serviceloader.mediator";
    String PROVIDER_IMPLCLASS_PROPERTY = ".org.apache.aries.spifly.provider.implclass";

    // The names of the extenders involved
    public static String PROCESSOR_EXTENDER_NAME = "osgi.serviceloader.processor";
    public static String REGISTRAR_EXTENDER_NAME = "osgi.serviceloader.registrar";

    public static final String METAINF_SERVICES = "META-INF/services";

    // Pre-baked requirements for consumer and provider
//    String CLIENT_REQUIREMENT = EXTENDER_CAPABILITY_NAMESPACE + "; " + FILTER_DIRECTIVE +
//            ":=\"(" + EXTENDER_CAPABILITY_NAMESPACE + "=" + PROCESSOR_EXTENDER_NAME + ")\"";
//    String PROVIDER_REQUIREMENT = EXTENDER_CAPABILITY_NAMESPACE + "; " + FILTER_DIRECTIVE +
//            ":=\"(" + EXTENDER_CAPABILITY_NAMESPACE + "=" + REGISTRAR_EXTENDER_NAME + ")\"";
}
