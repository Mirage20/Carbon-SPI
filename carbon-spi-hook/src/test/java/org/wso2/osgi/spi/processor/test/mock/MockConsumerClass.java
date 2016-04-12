package org.wso2.osgi.spi.processor.test.mock;

import java.util.ServiceLoader;

public class MockConsumerClass {
    public void requestService() {
        ServiceLoader<MockInterface> mockInterfaces = ServiceLoader.load(MockInterface.class);
        mockInterfaces.forEach(MockInterface::doSomething);
    }
}
