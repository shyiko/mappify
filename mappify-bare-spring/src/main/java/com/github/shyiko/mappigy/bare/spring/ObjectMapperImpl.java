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

/**
 * Implementation of {@link ObjectMapper} which discovers mappings from Spring Context using {@link MappingProvider}
 * annotated beans.
 * @author <a href="mailto:stanley.shyiko@gmail.com">Stanley Shyiko</a>
 */
@Component
public class ObjectMapperImpl implements ObjectMapper, BeanPostProcessor {

    private static final Logger logger = LoggerFactory.getLogger(ObjectMapperImpl.class);

    private Map<MappingKey, MappingValue> config = new HashMap<MappingKey, MappingValue>();
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
                if (method.isAnnotationPresent(Mapping.class)) {
                    Class<?>[] parameterTypes = method.getParameterTypes();
                    if (parameterTypes.length != 2) {
                        logger.warn(method + " is annotated with @Mapping but do not present a valid mapping");
                        continue;
                    }
                    Class sourceClass = parameterTypes[0];
                    Class targetClass = parameterTypes[1];
                    if (logger.isDebugEnabled()) {
                        logger.debug("Discovered mapping from " + sourceClass + " to " + targetClass);
                    }
                    config.put(new MappingKey(sourceClass, targetClass), new MappingValue(bean, method));
                }
            }
        }
        return bean;
    }

    @Override
    public <T> void map(Object source, T target) {
        if (source == null) {
            throw new IllegalArgumentException("Source object cannot be null");
        }
        if (target == null) {
            throw new IllegalArgumentException("Target object cannot be null");
        }
        MappingKey key = new MappingKey(deProxy(source), deProxy(target));
        MappingValue mapping = config.get(key);
        if (mapping == null) {
            MappingKey rootKey = key;
            do {
                Class sourceSuperClass = key.sourceClass.getSuperclass();
                if (sourceSuperClass == null) {
                    break;
                }
                key = new MappingKey(sourceSuperClass, key.targetClass);
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

    @Override
    public <T> T map(Object source, Class<T> targetClass) {
        if (source == null) {
            return null;
        }
        T result;
        try {
            result = targetClass.newInstance();
        } catch (Exception e) {
            throw new MappingException(targetClass + " has no default constructor", e);
        }
        map(source, result);
        return result;
    }

    @Override
    public <T> HashSet<T> mapToHashSet(Collection sources, Class<T> targetClass) {
        HashSet<T> result = new HashSet<T>();
        for (Object source : sources) {
            result.add(map(source, targetClass));
        }
        return result;
    }

    @Override
    public <T> TreeSet<T> mapToTreeSet(Collection sources, Class<T> targetClass) {
        TreeSet<T> result = new TreeSet<T>();
        for (Object source : sources) {
            result.add(map(source, targetClass));
        }
        return result;
    }

    @Override
    public <T> LinkedList<T> mapToLinkedList(Collection sources, Class<T> targetClass) {
        LinkedList<T> result = new LinkedList<T>();
        for (Object source : sources) {
            result.add(map(source, targetClass));
        }
        return result;
    }

    @Override
    public <T> ArrayList<T> mapToArrayList(Collection sources, Class<T> targetClass) {
        ArrayList<T> result = new ArrayList<T>(sources.size());
        for (Object source : sources) {
            result.add(map(source, targetClass));
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T[] mapToArray(Collection sources, Class<T> targetClass) {
        T[] result = (T[]) Array.newInstance(targetClass, sources.size());
        int i = 0;
        for (Object source : sources) {
            result[i++] = map(source, targetClass);
        }
        return result;
    }

    private Class deProxy(Object type) {
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

        private MappingKey(final Class sourceClass, final Class targetClass) {
            this.sourceClass = sourceClass;
            this.targetClass = targetClass;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            MappingKey that = (MappingKey) o;
            return sourceClass.equals(that.sourceClass) && targetClass.equals(that.targetClass);
        }

        @Override
        public int hashCode() {
            int result = sourceClass.hashCode();
            result = 31 * result + targetClass.hashCode();
            return result;
        }

        @Override
        public String toString() {
            return sourceClass + "->" + targetClass;
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
}
