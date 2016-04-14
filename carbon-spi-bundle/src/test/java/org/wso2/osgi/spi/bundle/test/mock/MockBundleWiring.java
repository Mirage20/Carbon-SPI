package org.wso2.osgi.spi.bundle.test.mock;

import org.osgi.framework.Bundle;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleRequirement;
import org.osgi.framework.wiring.BundleRevision;
import org.osgi.framework.wiring.BundleWire;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.resource.Capability;
import org.osgi.resource.Requirement;
import org.osgi.resource.Wire;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MockBundleWiring implements BundleWiring{

    List<BundleCapability> capabilities = new ArrayList<>();
    List<BundleRequirement> requirements = new ArrayList<>();

    public void setCapabilities(List<BundleCapability> capabilities) {
        this.capabilities = capabilities;
    }

    public void setRequirements(List<BundleRequirement> requirements) {
        this.requirements = requirements;
    }

    @Override
    public boolean isCurrent() {
        return false;
    }

    @Override
    public boolean isInUse() {
        return false;
    }

    @Override
    public List<BundleCapability> getCapabilities(String s) {
        return capabilities;
    }

    @Override
    public List<BundleRequirement> getRequirements(String s) {
        return requirements;
    }

    @Override
    public List<BundleWire> getProvidedWires(String s) {
        return null;
    }

    @Override
    public List<BundleWire> getRequiredWires(String s) {
        return null;
    }

    @Override
    public BundleRevision getRevision() {
        return null;
    }

    @Override
    public ClassLoader getClassLoader() {
        return null;
    }

    @Override
    public List<URL> findEntries(String s, String s1, int i) {
        return null;
    }

    @Override
    public Collection<String> listResources(String s, String s1, int i) {
        return null;
    }

    @Override
    public List<Capability> getResourceCapabilities(String s) {
        return null;
    }

    @Override
    public List<Requirement> getResourceRequirements(String s) {
        return null;
    }

    @Override
    public List<Wire> getProvidedResourceWires(String s) {
        return null;
    }

    @Override
    public List<Wire> getRequiredResourceWires(String s) {
        return null;
    }

    @Override
    public BundleRevision getResource() {
        return null;
    }

    @Override
    public Bundle getBundle() {
        return null;
    }
}
