package org.wso2.osgi.spi.processor.test;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.wso2.osgi.spi.Constants;
import org.wso2.osgi.spi.processor.ConsumerProcessor;
import org.wso2.osgi.spi.processor.test.mock.MockConsumerClass;
import org.wso2.osgi.spi.processor.test.mock.MockWovenClass;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class ConsumerProcessorTest {


    @Test
    public void testClassWeaving() throws IOException {

        ConsumerProcessor consumerProcessor = new ConsumerProcessor();
        String buildDir = System.getProperty("project.build.directory");

        Path mockClass = Paths.get(buildDir, "test-classes", "org", "wso2", "osgi", "spi", "processor",
                "test", "mock", "MockConsumerClass.class");

        byte[] classBytes = Files.readAllBytes(mockClass);
        MockWovenClass mockWovenClass = new MockWovenClass(classBytes, MockConsumerClass.class.getName());
        consumerProcessor.weave(mockWovenClass);

        Assert.assertTrue(classBytes.length < mockWovenClass.getBytes().length, "Check if the mock class is woven.");
        Assert.assertTrue(mockWovenClass.getDynamicImports().contains(Constants.DYNAMIC_INJECT_PACKAGE_NAME),
                "Check whether the dynamic package import is added");

    }


}
