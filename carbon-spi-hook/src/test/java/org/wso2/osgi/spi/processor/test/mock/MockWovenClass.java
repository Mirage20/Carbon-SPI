package org.wso2.osgi.spi.processor.test.mock;

import org.osgi.framework.hooks.weaving.WovenClass;
import org.osgi.framework.wiring.BundleWiring;

import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.List;

public class MockWovenClass implements WovenClass {

    private byte[] classBytes;
    private String className;
    private List<String> dynamicImports = new ArrayList<>();

    public MockWovenClass(byte[] classBytes, String className) {
        this.classBytes = classBytes;
        this.className = className;
    }

    @Override
    public byte[] getBytes() {
        return classBytes;
    }

    @Override
    public void setBytes(byte[] bytes) {
        classBytes = bytes;
    }

    @Override
    public List<String> getDynamicImports() {
        return dynamicImports;
    }

    @Override
    public boolean isWeavingComplete() {
        return false;
    }

    @Override
    public String getClassName() {
        return className;
    }

    @Override
    public ProtectionDomain getProtectionDomain() {
        return null;
    }

    @Override
    public Class<?> getDefinedClass() {
        return null;
    }

    @Override
    public BundleWiring getBundleWiring() {
        return new MockBundleWiring();
    }

    @Override
    public int getState() {
        return 0;
    }
}
