package org.wso2.osgi.spi.bundle.test.mock;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.Version;
import org.osgi.framework.wiring.BundleWiring;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

public class MockBundle implements Bundle {

    BundleWiring wiring;

    public void setWiring(BundleWiring wiring) {
        this.wiring = wiring;
    }

    @Override
    public int getState() {
        return 0;
    }

    @Override
    public void start(int i) throws BundleException {

    }

    @Override
    public void start() throws BundleException {

    }

    @Override
    public void stop(int i) throws BundleException {

    }

    @Override
    public void stop() throws BundleException {

    }

    @Override
    public void update(InputStream inputStream) throws BundleException {

    }

    @Override
    public void update() throws BundleException {

    }

    @Override
    public void uninstall() throws BundleException {

    }

    @Override
    public Dictionary<String, String> getHeaders() {
        return null;
    }

    @Override
    public long getBundleId() {
        return 0;
    }

    @Override
    public String getLocation() {
        return null;
    }

    @Override
    public ServiceReference<?>[] getRegisteredServices() {
        return new ServiceReference<?>[0];
    }

    @Override
    public ServiceReference<?>[] getServicesInUse() {
        return new ServiceReference<?>[0];
    }

    @Override
    public boolean hasPermission(Object o) {
        return false;
    }

    @Override
    public URL getResource(String s) {
        return null;
    }

    @Override
    public Dictionary<String, String> getHeaders(String s) {
        return null;
    }

    @Override
    public String getSymbolicName() {
        return null;
    }

    @Override
    public Class<?> loadClass(String s) throws ClassNotFoundException {
        return null;
    }

    @Override
    public Enumeration<URL> getResources(String s) throws IOException {
        return null;
    }

    @Override
    public Enumeration<String> getEntryPaths(String s) {
        return null;
    }

    @Override
    public URL getEntry(String s) {
        return null;
    }

    @Override
    public long getLastModified() {
        return 0;
    }

    @Override
    public Enumeration<URL> findEntries(String s, String s1, boolean b) {
        return Collections.enumeration(Collections.emptyList());
    }

    @Override
    public BundleContext getBundleContext() {
        return null;
    }

    @Override
    public Map<X509Certificate, List<X509Certificate>> getSignerCertificates(int i) {
        return null;
    }

    @Override
    public Version getVersion() {
        return null;
    }

    @Override
    public <A> A adapt(Class<A> aClass) {
        return (A)wiring;
    }

    @Override
    public File getDataFile(String s) {
        return null;
    }

    @Override
    public int compareTo(Bundle o) {
        return 0;
    }
}
