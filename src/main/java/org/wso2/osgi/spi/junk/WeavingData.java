/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.osgi.spi.junk;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/** Contains information needed for the byte code weaver.
 */
public class WeavingData {
    private final String className;
    private final String methodName;
    private final String[] argClasses;
//    private final Set<ConsumerRestriction> argRestrictions;
//    private final List<BundleDescriptor> allowedBundles;


    public WeavingData() {
        // TODO can we infer argClasses from restrictions?
        this.className = "java.util.ServiceLoader";
        this.methodName = "load";
        this.argClasses = new String []{"java.lang.Class"};
//        this.argRestrictions = argRestrictions;
//        this.allowedBundles = allowedBundles;
    }

    public String getClassName() {
        return className;
    }

    public String getMethodName() {
        return methodName;
    }


    public String[] getArgClasses() {
        return argClasses;
    }





}
