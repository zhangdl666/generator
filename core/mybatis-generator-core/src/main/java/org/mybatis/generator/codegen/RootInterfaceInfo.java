/**
 *    Copyright 2006-2019 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.mybatis.generator.codegen;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.internal.ObjectFactory;

import java.beans.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mybatis.generator.internal.util.messages.Messages.getString;

/**
 * Holds information about a interface
 * @author zdl
 * 
 */
public class RootInterfaceInfo {

    private static Map<String, RootInterfaceInfo> rootInterfaceInfoMap;

    static {
        rootInterfaceInfoMap = Collections
                .synchronizedMap(new HashMap<String, RootInterfaceInfo>());
    }

    public static RootInterfaceInfo getInstance(String className,
                                                List<String> warnings) {
        return rootInterfaceInfoMap.computeIfAbsent(className, k -> new RootInterfaceInfo(k, warnings));
    }

    /**
     * Clears the internal map containing root class info.  This method should be called at the beginning of
     * a generation run to clear the cached root class info in case there has been a change.
     * For example, when using the eclipse launcher, the cache would be kept until eclipse
     * was restarted.
     *
     */
    public static void reset() {
        rootInterfaceInfoMap.clear();
    }

    private MethodDescriptor[] methodDescriptors;
    private String className;
    private List<String> warnings;
    private boolean genericMode = false;

    private RootInterfaceInfo(String className, List<String> warnings) {
        super();
        this.className = className;
        this.warnings = warnings;

        if (className == null) {
            return;
        }

        FullyQualifiedJavaType fqjt = new FullyQualifiedJavaType(className);
        String nameWithoutGenerics = fqjt.getFullyQualifiedNameWithoutTypeParameters();
        if (!nameWithoutGenerics.equals(className)) {
            genericMode = true;
        }

        try {
            Class<?> clazz = ObjectFactory.externalClassForName(nameWithoutGenerics);
            BeanInfo bi = Introspector.getBeanInfo(clazz);
            methodDescriptors = bi.getMethodDescriptors();
        } catch (Exception e) {
            e.printStackTrace();
            methodDescriptors = null;
            warnings.add(getString("Warning.31", className)); //$NON-NLS-1$
        }
    }

    public boolean containsMethod(String methodName) {
        if (methodDescriptors == null) {
            return false;
        }

        boolean found = false;

        // get method names from class and check against this column definition.
        // better yet, have a map of method Names. check against it.
        for (int i = 0; i < methodDescriptors.length; i++) {
            MethodDescriptor methodDescriptor = methodDescriptors[i];

            if (methodDescriptor.getMethod().getName().equals(methodName)) {
                found = true;
                break;
            }
        }

        return found;
    }
}
