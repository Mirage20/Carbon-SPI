package org.wso2.osgi.spi.processor;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

/**
 * Class loader which combines multiple class loaders.
 */
public class CombinedClassLoader extends ClassLoader {

    private final List<ClassLoader> bundleClassLoaders;

    protected CombinedClassLoader(final List<ClassLoader> bundleClassLoaders) {
        this.bundleClassLoaders = bundleClassLoaders;
    }

    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        List<URL> resourceUrls = new ArrayList<>();
        for (ClassLoader bundleClassLoader : bundleClassLoaders) {
            resourceUrls.addAll(Collections.list(bundleClassLoader.getResources(name)));
        }
        return Collections.enumeration(resourceUrls);
    }

    @Override
    public URL getResource(String name) {
        for (ClassLoader bundleClassLoader : bundleClassLoaders) {
            URL resource = bundleClassLoader.getResource(name);
            if (resource != null) {
                return resource;
            }
        }
        return null;
    }


    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        for (ClassLoader bundleClassLoader : bundleClassLoaders) {
            try {
                return bundleClassLoader.loadClass(name);
            } catch (ClassNotFoundException ex) {
                // Try next classloader
            }
        }

        throw new ClassNotFoundException(name);
    }

    @Override
    public InputStream getResourceAsStream(String name) {
        for (ClassLoader bundleClassLoader : bundleClassLoaders) {
            InputStream is = bundleClassLoader.getResourceAsStream(name);
            if (is != null)
                return is;
        }
        return null;
    }
}
