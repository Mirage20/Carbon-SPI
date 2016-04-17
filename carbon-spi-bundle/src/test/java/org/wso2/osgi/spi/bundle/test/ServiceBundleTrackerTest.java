package org.wso2.osgi.spi.bundle.test;


import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleRequirement;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.osgi.spi.bundle.test.mock.MockBundle;
import org.wso2.osgi.spi.bundle.test.mock.MockBundleCapability;
import org.wso2.osgi.spi.bundle.test.mock.MockBundleContext;
import org.wso2.osgi.spi.bundle.test.mock.MockBundleRequirement;
import org.wso2.osgi.spi.bundle.test.mock.MockBundleWiring;
import org.wso2.osgi.spi.internal.Constants;
import org.wso2.osgi.spi.internal.ServiceBundleTracker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiceBundleTrackerTest {

    ServiceBundleTracker bundleTracker;

    @BeforeClass
    public void setup() {

        MockBundle mockMediatorBundle = new MockBundle();
        MockBundleWiring mockBundleWiring = new MockBundleWiring();

        List<BundleCapability> bundleCapabilityList = new ArrayList<>();

        MockBundleCapability mockMediatorProcessorCapability = new MockBundleCapability();
        Map<String, Object> attr1 = new HashMap<>();
        attr1.put(Constants.EXTENDER_CAPABILITY_NAMESPACE, Constants.PROCESSOR_EXTENDER_NAME);
        mockMediatorProcessorCapability.setAttributes(attr1);
        bundleCapabilityList.add(mockMediatorProcessorCapability);

        MockBundleCapability mockMediatorRegistrarCapability = new MockBundleCapability();
        Map<String, Object> attr2 = new HashMap<>();
        attr2.put(Constants.EXTENDER_CAPABILITY_NAMESPACE, Constants.REGISTRAR_EXTENDER_NAME);
        mockMediatorRegistrarCapability.setAttributes(attr2);
        bundleCapabilityList.add(mockMediatorRegistrarCapability);

        mockBundleWiring.setCapabilities(bundleCapabilityList);

        mockMediatorBundle.setWiring(mockBundleWiring);
        MockBundleContext mockBundleContext = new MockBundleContext(mockMediatorBundle);
        bundleTracker = new ServiceBundleTracker(mockBundleContext, 0);
    }

    @Test
    public void testAddProviders() {
        MockBundle mockProviderBundle = new MockBundle();
        MockBundleWiring mockBundleWiring = new MockBundleWiring();

        List<BundleCapability> bundleCapabilityList = new ArrayList<>();

        MockBundleCapability mockProviderCapability = new MockBundleCapability();
        Map<String, Object> attr1 = new HashMap<>();
        attr1.put(Constants.SERVICELOADER_NAMESPACE, Constants.SERVICELOADER_NAMESPACE);
        mockProviderCapability.setAttributes(attr1);
        bundleCapabilityList.add(mockProviderCapability);


        List<BundleRequirement> bundleRequirementList = new ArrayList<>();

        MockBundleRequirement mockMediatorRegistrarCapability = new MockBundleRequirement();
        Map<String, Object> attr2 = new HashMap<>();
        attr2.put(Constants.EXTENDER_CAPABILITY_NAMESPACE, Constants.REGISTRAR_EXTENDER_NAME);
        mockMediatorRegistrarCapability.setAttributes(attr2);
        bundleRequirementList.add(mockMediatorRegistrarCapability);

        mockBundleWiring.setCapabilities(bundleCapabilityList);
        mockBundleWiring.setRequirements(bundleRequirementList);

        mockProviderBundle.setWiring(mockBundleWiring);

        bundleTracker.addingBundle(mockProviderBundle, null);

        Assert.assertEquals(bundleTracker.getProvider(mockProviderBundle).getProviderBundle(), mockProviderBundle,
                "Test provider bundle is added");
        Assert.assertTrue(bundleTracker.isProvider(mockProviderBundle), "Test the added bundle is the provider.");
        Assert.assertFalse(bundleTracker.isConsumer(mockProviderBundle), "Test the added bundle is a consumer.");
    }


    @Test
    public void testAddConsumers() {
        MockBundle mockConsumerBundle = new MockBundle();
        MockBundleWiring mockBundleWiring = new MockBundleWiring();


        List<BundleRequirement> bundleRequirementList = new ArrayList<>();

        MockBundleRequirement mockMediatorProcessorCapability = new MockBundleRequirement();
        Map<String, Object> attr2 = new HashMap<>();
        attr2.put(Constants.EXTENDER_CAPABILITY_NAMESPACE, Constants.PROCESSOR_EXTENDER_NAME);
        mockMediatorProcessorCapability.setAttributes(attr2);
        bundleRequirementList.add(mockMediatorProcessorCapability);

        mockBundleWiring.setRequirements(bundleRequirementList);

        mockConsumerBundle.setWiring(mockBundleWiring);

        bundleTracker.addingBundle(mockConsumerBundle, null);

        Assert.assertEquals(bundleTracker.getConsumer(mockConsumerBundle).getConsumerBundle(), mockConsumerBundle,
                "Test consumer bundle is added");
        Assert.assertTrue(bundleTracker.isConsumer(mockConsumerBundle), "Test the added bundle is the consumer.");
        Assert.assertFalse(bundleTracker.isProvider(mockConsumerBundle), "Test the added bundle is a provider.");
    }
}
