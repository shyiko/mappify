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
package com.github.shyiko.mappigy.bare.spring;

import com.github.shyiko.mappify.api.MappingException;
import com.github.shyiko.mappify.api.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe implementation of {@link ObjectMapper} which discovers mappings by scanning Spring context
 * for {@link MappingProvider} annotated beans.
 * @author <a href="mailto:stanley.shyiko@gmail.com">Stanley Shyiko</a>
 */
@Component
public class ObjectMapperImpl implements ObjectMapper, BeanPostProcessor {

    private static final Logger logger = LoggerFactory.getLogger(ObjectMapperImpl.class);
    private static final com.github.shyiko.mappify.api.Mapping[] DEFAULT_MAPPINGS = new com.github.shyiko.mappify.api.Mapping[0];

    private Map<MappingKey, MappingValue> config = new ConcurrentHashMap<MappingKey, MappingValue>();
    private Method hibernateProxyHelperNarrowMethod;

    public ObjectMapperImpl() {
        String hibernateProxyHelperClass = "org.hibernate.proxy.HibernateProxyHelper",
               hibernateProxyHelperNarrowMethodName = "getClassWithoutInitializingProxy";
        try {
            Class<?> hibernateProxyHelper = Class.forName(hibernateProxyHelperClass);
            hibernateProxyHelperNarrowMethod = hibernateProxyHelper.getMethod(hibernateProxyHelperNarrowMethodName, Object.class);
        } catch (ClassNotFoundException e) {
            if (logger.isDebugEnabled()) {
                logger.debug(hibernateProxyHelperClass + " wasn't loaded. Hibernate proxy narrowing disabled.");
            }
        } catch (NoSuchMethodException e) {
            if (logger.isDebugEnabled()) {
                logger.debug(hibernateProxyHelperClass + " has no method named " + hibernateProxyHelperNarrowMethodName +
                        ". Hibernate proxy narrowing disabled.");
            }
        }
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean.getClass().isAnnotationPresent(MappingProvider.class)) {
            Method[] methods = bean.getClass().getMethods();
            for (Method method : methods) {
                Mapping mapping = method.getAnnotation(Mapping.class);
                if (mapping != null) {
                    Class<?>[] parameterTypes = method.getParameterTypes();
                    if (parameterTypes.length != 2) {
                        logger.warn(method + " is annotated with @Mapping but do not present a valid mapping");
                        continue;
                    }
                    Class sourceClass = parameterTypes[0];
                    Class targetClass = parameterTypes[1];
                    String mappingName = mapping.value();
                    if (logger.isDebugEnabled()) {
                        StringBuilder message = new StringBuilder("Discovered mapping ");
                        if (!"".equals(mappingName)) {
                            message.append("'").append(mappingName).append("' ");
                        }
                         message.append("from ").append(sourceClass).append(" to ").append(targetClass);
                        logger.debug(message.toString());
                    }
                    config.put(new MappingKey(sourceClass, targetClass, mappingName), new MappingValue(bean, method));
                }
            }
        }
        return bean;
    }

    @Override
    public ObjectMapper using(final com.github.shyiko.mappify.api.Mapping... mapping) {
        return new ObjectMapperClosure(mapping, this);
    }

    @Override
    public <T> void map(Object source, T target) {
        map(source, target, DEFAULT_MAPPINGS);
    }

    protected <T> void map(Object source, T target, com.github.shyiko.mappify.api.Mapping[] mappings) {
        if (source == null) {
            throw new IllegalArgumentException("Source object cannot be null");
        }
        if (target == null) {
            throw new IllegalArgumentException("Target object cannot be null");
        }
        MappingKey key = new MappingKey(deProxy(source), deProxy(target), getMappingName(mappings));
        MappingValue mapping = config.get(key);
        if (mapping == null) {
            MappingKey rootKey = key;
            do {
                Class sourceSuperClass = key.sourceClass.getSuperclass();
                if (sourceSuperClass == null) {
                    break;
                }
                key = new MappingKey(sourceSuperClass, key.targetClass, getMappingName(mappings));
                mapping = config.get(key);
            } while (mapping == null);
            if (mapping == null) {
                throw new MappingException("Mapping from " + rootKey.sourceClass + " to " +
                        rootKey.targetClass + " has not been defined");
            }
            config.put(key, mapping); // self-training
        }
        try {
            mapping.method.invoke(mapping.bean, source, target);
        } catch (Exception e) {
            throw new MappingException("Unable to map " + key.sourceClass + " to " + key.targetClass, e);
        }
    }

    private String getMappingName(com.github.shyiko.mappify.api.Mapping[] mappings) {
        if (mappings != DEFAULT_MAPPINGS) {
            for (com.github.shyiko.mappify.api.Mapping mapping : mappings) {
                if (mapping instanceof com.github.shyiko.mappify.api.Mapping.NamedMapping) {
                    return ((com.github.shyiko.mappify.api.Mapping.NamedMapping) mapping).getMappingName();
                }
            }
        }
        return "";
    }

    @Override
    public <T> T map(Object source, Class<T> targetClass) {
        return map(source, targetClass, DEFAULT_MAPPINGS);
    }

    protected <T> T map(Object source, Class<T> targetClass, com.github.shyiko.mappify.api.Mapping[] mappings) {
        if (source == null) {
            return null;
        }
        T result;
        try {
            result = targetClass.newInstance();
        } catch (Exception e) {
            throw new MappingException(targetClass + " has no default constructor", e);
        }
        map(source, result, mappings);
        return result;
    }

    @Override
    public <T> HashSet<T> mapToHashSet(Collection sources, Class<T> targetClass) {
        return mapToHashSet(sources, targetClass, DEFAULT_MAPPINGS);
    }

    protected  <T> HashSet<T> mapToHashSet(Collection sources, Class<T> targetClass, com.github.shyiko.mappify.api.Mapping[] mappings) {
        HashSet<T> result = new HashSet<T>();
        for (Object source : sources) {
            result.add(map(source, targetClass, mappings));
        }
        return result;
    }

    @Override
    public <T> TreeSet<T> mapToTreeSet(Collection sources, Class<T> targetClass) {
        return mapToTreeSet(sources, targetClass, DEFAULT_MAPPINGS);
    }

    protected <T> TreeSet<T> mapToTreeSet(Collection sources, Class<T> targetClass, com.github.shyiko.mappify.api.Mapping[] mappings) {
        TreeSet<T> result = new TreeSet<T>();
        for (Object source : sources) {
            result.add(map(source, targetClass, mappings));
        }
        return result;
    }

    @Override
    public <T> LinkedList<T> mapToLinkedList(Collection sources, Class<T> targetClass) {
        return mapToLinkedList(sources, targetClass, DEFAULT_MAPPINGS);
    }

    protected <T> LinkedList<T> mapToLinkedList(Collection sources, Class<T> targetClass, com.github.shyiko.mappify.api.Mapping[] mappings) {
        LinkedList<T> result = new LinkedList<T>();
        for (Object source : sources) {
            result.add(map(source, targetClass, mappings));
        }
        return result;
    }

    @Override
    public <T> ArrayList<T> mapToArrayList(Collection sources, Class<T> targetClass) {
        return mapToArrayList(sources, targetClass, DEFAULT_MAPPINGS);
    }

    protected <T> ArrayList<T> mapToArrayList(Collection sources, Class<T> targetClass, com.github.shyiko.mappify.api.Mapping[] mappings) {
        ArrayList<T> result = new ArrayList<T>(sources.size());
        for (Object source : sources) {
            result.add(map(source, targetClass, mappings));
        }
        return result;
    }

    @Override
    public <T> T[] mapToArray(Collection sources, Class<T> targetClass) {
        return mapToArray(sources, targetClass, DEFAULT_MAPPINGS);
    }

    @SuppressWarnings("unchecked")
    protected <T> T[] mapToArray(Collection sources, Class<T> targetClass, com.github.shyiko.mappify.api.Mapping[] mappings) {
        T[] result = (T[]) Array.newInstance(targetClass, sources.size());
        int i = 0;
        for (Object source : sources) {
            result[i++] = map(source, targetClass, mappings);
        }
        return result;
    }

    protected Class deProxy(Object type) {
        if (hibernateProxyHelperNarrowMethod != null) {
            try {
                return (Class) hibernateProxyHelperNarrowMethod.invoke(null, type);
            } catch (IllegalAccessException e) {
                throw new MappingException("Failed to narrow Hibernate proxy", e);
            } catch (InvocationTargetException e) {
                throw new MappingException("Failed to narrow Hibernate proxy", e);
            }
        }
        return type.getClass();
    }

    private static final class MappingKey {

        private final Class sourceClass;
        private final Class targetClass;
        private final String mappingName;

        private MappingKey(final Class sourceClass, final Class targetClass, String mappingName) {
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
            sb.append(sourceClass).append("->").append(targetClass);
            if (!"".equals(mappingName)) {
                sb.append(" ('").append(mappingName).append("')");
            }
            return sb.toString();
        }
    }

    private static final class MappingValue {

        private final Object bean;
        private final Method method;

        private MappingValue(Object bean, Method method) {
            this.bean = bean;
            this.method = method;
        }
    }

    private static class ObjectMapperClosure extends ObjectMapperImpl {

        private com.github.shyiko.mappify.api.Mapping[] mappings;
        private ObjectMapperImpl objectMapper;

        private ObjectMapperClosure(com.github.shyiko.mappify.api.Mapping[] mappings, ObjectMapperImpl objectMapper) {
            this.mappings = mappings;
            this.objectMapper = objectMapper;
        }

        @Override
        public ObjectMapper using(com.github.shyiko.mappify.api.Mapping... mapping) {
            return new ObjectMapperClosure(mappings, this);
        }

        @Override
        public <T> void map(Object source, T target) {
            objectMapper.map(source, target, mappings);
        }

        @Override
        public <T> T map(Object source, Class<T> targetClass) {
            return objectMapper.map(source, targetClass, mappings);
        }

        @Override
        public <T> HashSet<T> mapToHashSet(Collection sources, Class<T> targetClass) {
            return objectMapper.mapToHashSet(sources, targetClass, mappings);
        }

        @Override
        public <T> TreeSet<T> mapToTreeSet(Collection sources, Class<T> targetClass) {
            return objectMapper.mapToTreeSet(sources, targetClass, mappings);
        }

        @Override
        public <T> LinkedList<T> mapToLinkedList(Collection sources, Class<T> targetClass) {
            return objectMapper.mapToLinkedList(sources, targetClass, mappings);
        }

        @Override
        public <T> ArrayList<T> mapToArrayList(Collection sources, Class<T> targetClass) {
            return objectMapper.mapToArrayList(sources, targetClass, mappings);
        }

        @Override
        public <T> T[] mapToArray(Collection sources, Class<T> targetClass) {
            return objectMapper.mapToArray(sources, targetClass, mappings);
        }
    }
}
