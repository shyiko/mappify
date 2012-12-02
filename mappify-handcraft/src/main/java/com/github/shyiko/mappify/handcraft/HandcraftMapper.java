/*
 * Copyright 2012 Stanley Shyiko
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.shyiko.mappify.handcraft;

import com.github.shyiko.mappify.api.AbstractMapper;
import com.github.shyiko.mappify.api.MappingContext;
import com.github.shyiko.mappify.api.MappingException;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simplest {@link com.github.shyiko.mappify.api.Mapper} implementation which delegates mappings to the
 * {@link Mapping}-annotated methods.
 *
 * @author <a href="mailto:stanley.shyiko@gmail.com">Stanley Shyiko</a>
 */
public class HandcraftMapper extends AbstractMapper {

    protected Map<MappingIdentifier, MappingDelegate> config = new ConcurrentHashMap<MappingIdentifier, MappingDelegate>();
    private ProxyNarrowingStrategy proxyNarrowingStrategy = new HibernateProxyNarrowingStrategy();

    public ProxyNarrowingStrategy getProxyNarrowingStrategy() {
        return proxyNarrowingStrategy;
    }

    public void setProxyNarrowingStrategy(ProxyNarrowingStrategy proxyNarrowingStrategy) {
        this.proxyNarrowingStrategy = proxyNarrowingStrategy;
    }

    @Override
    public <T> T map(Object source, T target, String mappingName, MappingContext mappingContext) {
        assertNotNull(source, "Source object cannot be null");
        assertNotNull(target, "Target object cannot be null");
        assertNotNull(mappingName, "Mapping name cannot be null");
        MappingIdentifier key = new MappingIdentifier(proxyNarrowingStrategy.narrow(source),
                proxyNarrowingStrategy.narrow(target), mappingName);
        MappingDelegate mapping = findMappingDelegate(key);
        if (mapping == null) {
            throw new MappingNotFoundException("Mapping from " + key.sourceClass + " to " +
                    key.targetClass + " has not been defined");
        }
        try {
            mapping.invoke(source, target, mappingContext);
        } catch (Exception e) {
            throw new MappingException("Unable to map " + key.sourceClass + " to " + key.targetClass, e);
        }
        return target;
    }

    protected void assertNotNull(Object object, String exceptionMessage) {
        if (object == null) {
            throw new MappingException(exceptionMessage);
        }
    }

    @Override
    public boolean allowsToMap(Class sourceClass, Class targetClass, String mappingName) {
        MappingIdentifier key = new MappingIdentifier(sourceClass, targetClass, mappingName);
        return findMappingDelegate(key) != null;
    }

    /**
     * @param mappingProvider arbitrary object with {@link Mapping}-annotated methods.
     * @return collection of discovered mappings
     * @throws IllegalMappingDefinitionException if any of {@link Mapping}-annotated methods isn't valid
     */
    public Collection<MappingIdentifier> loadMappingsFrom(Object mappingProvider) {
        Collection<MappingIdentifier> result = new LinkedList<MappingIdentifier>();
        Method[] methods = mappingProvider.getClass().getDeclaredMethods();
        for (Method method : methods) {
            Mapping mapping = method.getAnnotation(Mapping.class);
            if (mapping != null) {
                Class<?>[] parameterTypes = method.getParameterTypes();
                if (parameterTypes.length != 2 && !(parameterTypes.length == 3 &&
                        MappingContext.class.isAssignableFrom(parameterTypes[2]))) {
                    throw new IllegalMappingDefinitionException(
                            method + " is annotated with @Mapping but doesn't denote a valid mapping");
                }
                MappingIdentifier key = new MappingIdentifier(parameterTypes[0], parameterTypes[1], mapping.value());
                config.put(key, new ReflectionBasedMappingDelegate(mappingProvider, method, parameterTypes.length == 3));
                result.add(key);
            }
        }
        return result;
    }

    protected MappingDelegate findMappingDelegate(MappingIdentifier key) {
        MappingDelegate mapping = config.get(key);
        if (mapping == null) {
            do {
                Class sourceSuperClass = key.sourceClass.getSuperclass();
                if (sourceSuperClass == null) {
                    break;
                }
                key = new MappingIdentifier(sourceSuperClass, key.targetClass, key.mappingName);
                mapping = config.get(key);
            } while (mapping == null);
            if (mapping != null) {
                config.put(key, mapping); // self-training
            }
        }
        return mapping;
    }

    public static final class MappingIdentifier {

        private final Class sourceClass;
        private final Class targetClass;
        private final String mappingName;

        public MappingIdentifier(final Class sourceClass, final Class targetClass, String mappingName) {
            this.sourceClass = sourceClass;
            this.targetClass = targetClass;
            this.mappingName = mappingName;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof MappingIdentifier)) return false;
            MappingIdentifier that = (MappingIdentifier) o;
            return mappingName.equals(that.mappingName) &&
                    sourceClass.equals(that.sourceClass) &&
                    targetClass.equals(that.targetClass);
        }

        @Override
        public int hashCode() {
            int result = sourceClass.hashCode();
            result = 31 * result + targetClass.hashCode();
            result = 31 * result + mappingName.hashCode();
            return result;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(sourceClass).append("->").append(targetClass);
            if (!"".equals(mappingName)) {
                sb.append(" ('").append(mappingName).append("')");
            }
            return sb.toString();
        }
    }

    public interface MappingDelegate {

        void invoke(Object source, Object target, MappingContext mappingContext) throws Exception;
    }

    public static class ReflectionBasedMappingDelegate implements MappingDelegate {

        private final Object bean;
        private final Method method;
        private final boolean requiresContext;

        public ReflectionBasedMappingDelegate(Object bean, Method method, boolean requiresContext) {
            this.bean = bean;
            this.method = method;
            this.requiresContext = requiresContext;
        }

        @Override
        public void invoke(Object source, Object target, MappingContext mappingContext) throws Exception {
            Object[] arguments = requiresContext ?
                    new Object[]{source, target, mappingContext} : new Object[]{source, target};
            method.invoke(bean, arguments);
        }
    }
}
