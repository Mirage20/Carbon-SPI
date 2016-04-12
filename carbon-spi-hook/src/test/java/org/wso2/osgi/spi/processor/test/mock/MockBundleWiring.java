package org.wso2.osgi.spi.processor.test.mock;

import org.osgi.framework.Bundle;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleRequirement;
import org.osgi.framework.wiring.BundleRevision;
import org.osgi.framework.wiring.BundleWire;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.resource.Capability;
import org.osgi.resource.Requirement;
import org.osgi.resource.Wire;
import org.wso2.osgi.spi.Constants;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MockBundleWiring implements BundleWiring {
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
        return null;
    }

    @Override
    public List<BundleRequirement> getRequirements(String s) {
        List<BundleRequirement> bundleRequirements = new ArrayList<>();
        bundleRequirements.add(new BundleRequirement() {
            @Override
            public BundleRevision getRevision() {
                return null;
            }

            @Override
            public boolean matches(BundleCapability bundleCapability) {
                return bundleCapability.getAttributes().containsKey(Constants.EXTENDER_CAPABILITY_NAMESPACE);
            }

            @Override
            public String getNamespace() {
                return null;
            }

            @Override
            public Map<String, String> getDirectives() {
                return null;
            }

            @Override
            public Map<String, Object> getAttributes() {
                return null;
            }

            @Override
            public BundleRevision getResource() {
                return null;
            }
        });
        return bundleRequirements;
    }

    @Override
    public List<BundleWire> getProvidedWires(String s) {
        return null;
    }

    @Override
    public List<BundleWire> getRequiredWires(String s) {
        List<BundleWire> bundleWires = new ArrayList<>();
        bundleWires.add(new BundleWire() {
            @Override
            public BundleCapability getCapability() {
                return new BundleCapability() {
                    @Override
                    public BundleRevision getRevision() {
                        return null;
                    }

                    @Override
                    public String getNamespace() {
                        return null;
                    }

                    @Override
                    public Map<String, String> getDirectives() {
                        return null;
                    }

                    @Override
                    public Map<String, Object> getAttributes() {
                        Map<String, Object> attr = new HashMap<>();
                        attr.put(Constants.EXTENDER_CAPABILITY_NAMESPACE, Constants.PROCESSOR_EXTENDER_NAME);
                        return attr;
                    }

                    @Override
                    public BundleRevision getResource() {
                        return null;
                    }
                };
            }

            @Override
            public BundleRequirement getRequirement() {
                return null;
            }

            @Override
            public BundleWiring getProviderWiring() {
                return null;
            }

            @Override
            public BundleWiring getRequirerWiring() {
                return null;
            }

            @Override
            public BundleRevision getProvider() {
                return null;
            }

            @Override
            public BundleRevision getRequirer() {
                return null;
            }
        });
        return bundleWires;
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
        return new MockBundle();
    }
}
