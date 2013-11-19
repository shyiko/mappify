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

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;
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

    public static final String HINT_REUSE_MAPPING = "handcraft_mapper_hint:reuse_mapping";

    protected Map<MappingKey, Mapping> config = new ConcurrentHashMap<MappingKey, Mapping>();
    protected volatile ProxyNarrowingStrategy proxyNarrowingStrategy = new HibernateProxyNarrowingStrategy();

    public ProxyNarrowingStrategy getProxyNarrowingStrategy() {
        return proxyNarrowingStrategy;
    }

    public void setProxyNarrowingStrategy(ProxyNarrowingStrategy proxyNarrowingStrategy) {
        this.proxyNarrowingStrategy = proxyNarrowingStrategy;
    }

    @Override
    public <C extends Collection<T>, T> C map(
            Collection sourceCollection, Class<T> targetClass, C targetCollection, String mappingName,
            MappingContext mappingContext) {
        assertNotNull(sourceCollection, "Source collection must never be null");
        assertNotNull(targetClass, "Target class cannot be null");
        assertNotNull(mappingName, "Mapping name cannot be null");
        if (!sourceCollection.isEmpty()) {
            Iterator sourceCollectionIterator = sourceCollection.iterator();
            Object source = sourceCollectionIterator.next();
            Mapping mapping = resolveMapping(source, mappingName, mappingContext, targetClass);
            targetCollection.add(map(mapping, source, targetClass, mappingName, mappingContext));
            while (sourceCollectionIterator.hasNext()) {
                targetCollection.add(map(mapping, sourceCollectionIterator.next(), targetClass, mappingName,
                        mappingContext));
            }
        }
        return targetCollection;
    }

    @Override
    public <S, C extends Collection<T>, T> C map(S[] sourceArray, Class<T> targetClass, C targetCollection,
            String mappingName, MappingContext mappingContext) {
        assertNotNull(sourceArray, "Source array must never be null");
        assertNotNull(targetClass, "Target class cannot be null");
        assertNotNull(mappingName, "Mapping name cannot be null");
        if (sourceArray.length != 0) {
            Mapping mapping = resolveMapping(sourceArray[0], mappingName, mappingContext, targetClass);
            for (Object source : sourceArray) {
                targetCollection.add(map(mapping, source, targetClass, mappingName, mappingContext));
            }
        }
        return targetCollection;
    }

    @Override
    public <S, C extends Map<S, T>, T> C map(S[] sourceArray, Class<T> targetClass, C targetMap, String mappingName,
            MappingContext mappingContext) {
        assertNotNull(sourceArray, "Source array must never be null");
        assertNotNull(targetClass, "Target class cannot be null");
        assertNotNull(mappingName, "Mapping name cannot be null");
        if (sourceArray.length != 0) {
            Mapping mapping = resolveMapping(sourceArray[0], mappingName, mappingContext, targetClass);
            for (S source : sourceArray) {
                targetMap.put(source, map(mapping, source, targetClass, mappingName, mappingContext));
            }
        }
        return targetMap;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <S, T> T[] map(S[] sourceArray, Class<T> targetClass, String mappingName, MappingContext mappingContext) {
        assertNotNull(sourceArray, "Source array must never be null");
        assertNotNull(targetClass, "Target class cannot be null");
        assertNotNull(mappingName, "Mapping name cannot be null");
        int sourceArrayLength = sourceArray.length;
        T[] result = (T[]) Array.newInstance(targetClass, sourceArrayLength);
        if (sourceArrayLength != 0) {
            Mapping mapping = resolveMapping(sourceArray[0], mappingName, mappingContext, targetClass);
            for (int i = 0; i < sourceArrayLength; i++) {
                result[i] = map(mapping, sourceArray[i], targetClass, mappingName, mappingContext);
            }
        }
        return result;
    }

    @Override
    public <S, C extends Map<S, T>, T> C map(
            Collection<S> sourceCollection, Class<T> targetClass, C targetMap, String mappingName,
            MappingContext mappingContext) {
        assertNotNull(sourceCollection, "Source collection must never be null");
        assertNotNull(targetClass, "Target class cannot be null");
        assertNotNull(mappingName, "Mapping name cannot be null");
        if (!sourceCollection.isEmpty()) {
            Iterator<S> sourceCollectionIterator = sourceCollection.iterator();
            S source = sourceCollectionIterator.next();
            Mapping mapping = resolveMapping(source, mappingName, mappingContext, targetClass);
            targetMap.put(source, map(mapping, source, targetClass, mappingName, mappingContext));
            while (sourceCollectionIterator.hasNext()) {
                source = sourceCollectionIterator.next();
                targetMap.put(source, map(mapping, source, targetClass, mappingName, mappingContext));
            }
        }
        return targetMap;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T[] map(
            Collection sourceCollection, Class<T> targetClass, String mappingName, MappingContext mappingContext) {
        assertNotNull(sourceCollection, "Source collection must never be null");
        assertNotNull(targetClass, "Target class cannot be null");
        assertNotNull(mappingName, "Mapping name cannot be null");
        T[] result = (T[]) Array.newInstance(targetClass, sourceCollection.size());
        if (!sourceCollection.isEmpty()) {
            Iterator sourceCollectionIterator = sourceCollection.iterator();
            Object source = sourceCollectionIterator.next();
            Mapping mapping = resolveMapping(source, mappingName, mappingContext, targetClass);
            result[0] = map(mapping, source, targetClass, mappingName, mappingContext);
            int i = 1;
            while (sourceCollectionIterator.hasNext()) {
                result[i++] = map(mapping, sourceCollectionIterator.next(), targetClass, mappingName, mappingContext);
            }
        }
        return result;
    }

    protected <S, T> Mapping resolveMapping(S source, String mappingName, MappingContext mappingContext,
                                          Class<T> targetClass) {
        if (mappingContext.hasKey(HINT_REUSE_MAPPING)) {
            return loadMapping(new MappingKey(proxyNarrowingStrategy.narrow(source), targetClass, mappingName));
        }
        return null;
    }

    @Override
    public <T> T map(Object source, Class<T> targetClass, String mappingName, MappingContext mappingContext) {
        if (source == null) {
            return null;
        }
        assertNotNull(targetClass, "Target class cannot be null");
        assertNotNull(mappingName, "Mapping name cannot be null");
        MappingKey key = new MappingKey(proxyNarrowingStrategy.narrow(source), targetClass, mappingName);
        return map(loadMapping(key), source, (T) null, mappingContext);
    }

    @Override
    public <T> T map(Object source, T target, String mappingName, MappingContext mappingContext) {
        assertNotNull(source, "Source object cannot be null");
        assertNotNull(target, "Target object cannot be null");
        assertNotNull(mappingName, "Mapping name cannot be null");
        MappingKey key = new MappingKey(proxyNarrowingStrategy.narrow(source), proxyNarrowingStrategy.narrow(target),
                mappingName);
        return map(loadMapping(key), source, target, mappingContext);
    }

    protected <T> T map(Mapping mapping, Object source, Class<T> targetClass, String mappingName,
            MappingContext mappingContext) {
        if (mapping == null) {
            mapping = loadMapping(new MappingKey(proxyNarrowingStrategy.narrow(source), targetClass, mappingName));
        }
        return map(mapping, source, (T) null, mappingContext);
    }

    @SuppressWarnings("unchecked")
    protected <T> T map(Mapping mapping, Object source, T target, MappingContext mappingContext) {
        try {
            MappingDelegate delegate = mapping.delegate;
            if (delegate.returnsTarget) {
                if (target != null) {
                    throw new MappingException("'" + mapping.key + "' cannot be used for overlay mapping");
                }
                return (T) delegate.method.invoke(delegate.delegatee, delegate.requiresContext ?
                        new Object[]{source, mappingContext} : new Object[]{source});
            }
            if (target == null) {
                target = (T) newInstance(mapping.key.targetClass);
            }
            delegate.method.invoke(delegate.delegatee, delegate.requiresContext ?
                    new Object[]{source, target, mappingContext} : new Object[]{source, target});
            return target;
        } catch (Exception e) {
            Throwable throwable = e;
            if (e instanceof InvocationTargetException) {
                throwable = e.getCause();
            }
            throw new MappingException("Unable to perform \'" + mapping.key + "\' mapping", throwable);
        }
    }

    @Override
    public boolean allowsToMap(Class sourceClass, Class targetClass, String mappingName) {
        MappingKey key = new MappingKey(sourceClass, targetClass, mappingName);
        return findMapping(key) != null;
    }

    /**
     * @param mappingProvider arbitrary object with {@link Mapping}-annotated methods.
     * @return collection of discovered mappings
     * @throws IllegalMappingDefinitionException if any of {@link Mapping}-annotated methods isn't a valid mapping definition
     * @throws DuplicateMappingDefinitionException if there are duplicate mapping definitions
     */
    public Collection<MappingKey> register(Object mappingProvider) {
        Collection<MappingKey> result = new LinkedList<MappingKey>();
        Method[] methods = mappingProvider.getClass().getDeclaredMethods();
        for (Method method : methods) {
            com.github.shyiko.mappify.handcraft.Mapping mapping =
                    method.getAnnotation(com.github.shyiko.mappify.handcraft.Mapping.class);
            if (mapping != null) {
                result.add(register(mappingProvider, method, mapping.value()));
            }
        }
        return result;
    }

    protected MappingKey register(Object mappingProvider, Method method, String mappingName) {
        Class<?> returnType = method.getReturnType();
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (!isValidMapping(returnType, parameterTypes)) {
            throw new IllegalMappingDefinitionException(
                    method + " is annotated with @Mapping but doesn't denote a valid mapping");
        }
        MappingKey key = new MappingKey(parameterTypes[0], returnType == Void.TYPE ?
                parameterTypes[1] : returnType, mappingName);
        MappingDelegate mappingDelegate = new MappingDelegate(mappingProvider, method);
        assertNotAlreadyRegistered(key, mappingDelegate);
        config.put(key, new Mapping(key, mappingDelegate));
        return key;
    }

    protected boolean isValidMapping(Class<?> returnType, Class<?>[] parameterTypes) {
        int upperBound = returnType == Void.TYPE ? 3 : 2,
            numberOfParameters = parameterTypes.length;
        return (numberOfParameters == upperBound - 1  &&
                   !MappingContext.class.isAssignableFrom(parameterTypes[upperBound - 2])) ||
               (numberOfParameters == upperBound &&
                   MappingContext.class.isAssignableFrom(parameterTypes[upperBound - 1]));
    }

    protected void assertNotAlreadyRegistered(MappingKey key, MappingDelegate mappingDelegate) {
        Mapping previousMapping = config.get(key);
        if (previousMapping != null) {
            throw new DuplicateMappingDefinitionException("Found duplicate mapping definitions: '" +
                    previousMapping.delegate + "' and '" + mappingDelegate + "'");
        }
    }

    protected Mapping findMapping(MappingKey requestedKey) {
        Mapping mapping = config.get(requestedKey);
        if (mapping == null) {
            MappingKey key = requestedKey;
            do {
                Class sourceSuperClass = key.sourceClass.getSuperclass();
                if (sourceSuperClass == null) {
                    break;
                }
                key = new MappingKey(sourceSuperClass, key.targetClass, key.mappingName);
                mapping = config.get(key);
            } while (mapping == null);
            if (mapping != null) {
                config.put(requestedKey, mapping); // self-training
            }
        }
        return mapping;
    }

    protected Mapping loadMapping(MappingKey requestedKey) {
        Mapping mapping = findMapping(requestedKey);
        if (mapping == null) {
            throw new MappingDefinitionNotFoundException("Stumbled upon undefined mapping \'" + requestedKey + "\'");
        }
        return mapping;
    }

    public static final class MappingKey {

        private final Class sourceClass;
        private final Class targetClass;
        private final String mappingName;

        public MappingKey(final Class sourceClass, final Class targetClass, String mappingName) {
            this.sourceClass = sourceClass;
            this.targetClass = targetClass;
            this.mappingName = mappingName;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof MappingKey)) return false;
            MappingKey that = (MappingKey) o;
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
            sb.append(sourceClass.getName()).append(" -> ").append(targetClass.getName());
            if (!"".equals(mappingName)) {
                sb.append(" ('").append(mappingName).append("')");
            }
            return sb.toString();
        }
    }

    private static final class MappingDelegate {

        private final Object delegatee;
        private final Method method;
        private final boolean requiresContext;
        private final boolean returnsTarget;

        public MappingDelegate(Object delegatee, Method method) {
            this.delegatee = delegatee;
            this.method = method;
            Class<?>[] parameterTypes = method.getParameterTypes();
            this.requiresContext = MappingContext.class.
                    isAssignableFrom(parameterTypes[parameterTypes.length - 1]);
            returnsTarget = method.getReturnType() != Void.TYPE;
        }

        @Override
        public String toString() {
            return method.toString();
        }
    }

    private static final class Mapping {

        private final MappingKey key;
        private final MappingDelegate delegate;

        public Mapping(MappingKey key, MappingDelegate delegate) {
            this.key = key;
            this.delegate = delegate;
        }
    }
}
