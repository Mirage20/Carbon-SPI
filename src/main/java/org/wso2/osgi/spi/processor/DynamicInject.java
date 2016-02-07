/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.osgi.spi.processor;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleReference;
import org.osgi.framework.wiring.BundleWiring;
import org.wso2.osgi.spi.internal.ServiceBundleTracker;
import org.wso2.osgi.spi.registrar.ProviderBundle;
import org.wso2.osgi.spi.internal.ServiceLoaderActivator;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Methods used from ASM-generated code. They store, change and reset the thread context classloader.
 * The methods are static to make it easy to access them from generated code.
 */
public class DynamicInject {

    static ThreadLocal<ClassLoader> storedClassLoaders = new ThreadLocal<>();

    // Provided as static method to make it easier to call from ASM-modified code
    public static void storeContextClassloader() {
        AccessController.doPrivileged(new PrivilegedAction<Void>() {
            public Void run() {
                storedClassLoaders.set(Thread.currentThread().getContextClassLoader());
                return null;
            }
        });

    }

    // Provided as static method to make it easier to call from ASM-modified code
    public static void restoreContextClassloader() {
        AccessController.doPrivileged(new PrivilegedAction<Void>() {
            public Void run() {
                Thread.currentThread().setContextClassLoader(storedClassLoaders.get());
                storedClassLoaders.set(null);
                return null;
            }
        });
    }

    public static void fixContextClassloader(Class<?> serviceType, ClassLoader consumerBundleLoader) {
//        if (BaseActivator.activator == null) {
//            // The system is not yet initialized. We can't do anything.
//            return;
//        }

        if (!(consumerBundleLoader instanceof BundleReference)) {
//            BaseActivator.activator.log(LogService.LOG_WARNING, "Classloader of consuming bundle doesn't implement BundleReference: " + consumerBundleLoader);
            return;
        }

        BundleReference br = ((BundleReference) consumerBundleLoader);

        final ClassLoader cl = findContextClassloader(br.getBundle(), serviceType);
        if (cl != null) {
//            BaseActivator.activator.log(LogService.LOG_INFO, "Temporarily setting Thread Context Classloader to: " + cl);
            AccessController.doPrivileged(new PrivilegedAction<Void>() {

                public Void run() {
                    Thread.currentThread().setContextClassLoader(cl);
                    return null;
                }
            });
        } else {
//            BaseActivator.activator.log(LogService.LOG_WARNING, "No classloader found for " + cls + ":" + method + "(" + serviceType + ")");
        }
    }

    private static ClassLoader findContextClassloader(Bundle consumerBundle, Class<?> clsArg) {

        ConsumerBundle cunsumer = ServiceLoaderActivator.getInstance().getServiceBundleTracker().getConsumer(consumerBundle);

        ServiceBundleTracker s = ServiceLoaderActivator.getInstance().getServiceBundleTracker();

        List<ProviderBundle> providerBundles = s.getMatchingProviders(clsArg.getName(), cunsumer);

        if (providerBundles.size() == 0) {
            return null;
        } else if (providerBundles.size() == 1) {
            return getProviderBundleClassLoader(providerBundles.get(0));
        } else {
            List<ClassLoader> loaders = new ArrayList<>();
            for (ProviderBundle b : providerBundles) {
                loaders.add(getProviderBundleClassLoader(b));
            }
            //return new MultiDelegationClassloader(loaders.toArray(new ClassLoader[loaders.size()]));
            return null;
        }
    }

    private static ClassLoader findContextClassloader(Bundle consumerBundle, String className, String methodName, Class<?> clsArg) {
//        BaseActivator activator = BaseActivator.activator;

//        String requestedClass;
//        Map<Pair<Integer, String>, String> args;
//        if (ServiceLoader.class.getName().equals(className) && "load".equals(methodName)) {
//            requestedClass = clsArg.getName();
//            args = new HashMap<Pair<Integer,String>, String>();
//            args.put(new Pair<Integer, String>(0, Class.class.getName()), requestedClass);
//
//            SecurityManager sm = System.getSecurityManager();
//            if (sm != null) {
//                try {
//                    sm.checkPermission(new ServicePermission(requestedClass, ServicePermission.GET));
//                } catch (AccessControlException ace) {
//                    // access denied
////                    activator.log(LogService.LOG_INFO, "No permission to obtain service of type: " + requestedClass);
//                    return null;
//                }
//            }
//        } else {
//            requestedClass = className;
//            args = null; // only supported on ServiceLoader.load() at the moment
//        }
//
//        List<ProviderBundle> providerBundles = ServiceLoaderActivator.getInstance().getServiceBundleTracker().getProviders();
////        activator.log(LogService.LOG_DEBUG, "Found providerBundles providing " + requestedClass + ": " + providerBundles);
//
//        Collection<Bundle> allowedBundles = null;

//        if (allowedBundles != null) {
//            for (Iterator<Bundle> it = providerBundles.iterator(); it.hasNext(); ) {
//                if (!allowedBundles.contains(it.next())) {
//                    it.remove();
//                }
//            }
//        }

//        switch (providerBundles.size()) {
//            case 0:
//                return null;
//            case 1:
//
//                return getProviderBundleClassLoader(providerBundles.get(0));
//            default:
//                List<ClassLoader> loaders = new ArrayList<ClassLoader>();
//                for (ProviderBundle b : providerBundles) {
//                    loaders.add(getProviderBundleClassLoader(b));
//                }
//                //return new MultiDelegationClassloader(loaders.toArray(new ClassLoader[loaders.size()]));
//                return null;
//        }
        return null;
    }

    private static ClassLoader getProviderBundleClassLoader(final ProviderBundle providerBundle) {
        return AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
            public ClassLoader run() {
                return providerBundle.getProviderBundle().adapt(BundleWiring.class).getClassLoader();
            }
        });
    }

}
