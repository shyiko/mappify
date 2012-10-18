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

import com.github.shyiko.mappify.api.MappingContext;
import com.github.shyiko.mappify.api.MappingException;
import com.github.shyiko.mappify.api.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.*;
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

    private String defaultMappingName = "";
    private Map<MappingKey, MappingValue> config = new ConcurrentHashMap<MappingKey, MappingValue>();
    private Method hibernateProxyHelperNarrowMethod;

    public ObjectMapperImpl() {
        String hibernateProxyHelperClass = "org.hibernate.proxy.HibernateProxyHelper",
               hibernateProxyHelperNarrowMethodName = "getClassWithoutInitializingProxy";
        try {
            Class<?> hibernateProxyHelper = Class.forName(hibernateProxyHelperClass);
            hibernateProxyHelperNarrowMethod = hibernateProxyHelper.getMethod(hibernateProxyHelperNarrowMethodName,
                    Object.class);
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
                    if (parameterTypes.length != 2 && !(parameterTypes.length == 3 &&
                            MappingContext.class.isAssignableFrom(parameterTypes[2]))) {
                        logger.warn(method + " is annotated with @Mapping but does not present a valid mapping");
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
                    config.put(
                            new MappingKey(sourceClass, targetClass, mappingName),
                            new MappingValue(bean, method, parameterTypes.length == 3)
                    );
                }
            }
        }
        return bean;
    }

    protected MappingContext getEmptyContext() {
        return new MappingContext();
    }

    @Override
    public ObjectMapper using(final MappingContext context) {
        return new ObjectMapperClosure(context);
    }

    @Override
    public <T> T map(Object source, T target) {
        return map(source, target, defaultMappingName);
    }

    protected <T> T map(Object source, T target, MappingContext context) {
        return map(source, target, defaultMappingName, context);
    }

    @Override
    public <T> T map(Object source, T target, String mappingName) {
        return map(source, target, mappingName, getEmptyContext());
    }

    protected <T> T map(Object source, T target, String mappingName, MappingContext context) {
        if (source == null) {
            throw new IllegalArgumentException("Source object cannot be null");
        }
        if (target == null) {
            throw new IllegalArgumentException("Target object cannot be null");
        }
        MappingKey key = new MappingKey(deProxy(source), deProxy(target), mappingName);
        MappingValue mapping = config.get(key);
        if (mapping == null) {
            MappingKey rootKey = key;
            do {
                Class sourceSuperClass = key.sourceClass.getSuperclass();
                if (sourceSuperClass == null) {
                    break;
                }
                key = new MappingKey(sourceSuperClass, key.targetClass, mappingName);
                mapping = config.get(key);
            } while (mapping == null);
            if (mapping == null) {
                throw new MappingException("Mapping from " + rootKey.sourceClass + " to " +
                        rootKey.targetClass + " has not been defined");
            }
            config.put(key, mapping); // self-training
        }
        try {
            Object[] arguments = mapping.requiresContext ?
                    new Object[] {source, target, context} : new Object[] {source, target};
            mapping.method.invoke(mapping.bean, arguments);
        } catch (Exception e) {
            throw new MappingException("Unable to map " + key.sourceClass + " to " + key.targetClass, e);
        }
        return target;
    }

    @Override
    public <T> T map(Object source, Class<T> targetClass) {
        return map(source, targetClass, defaultMappingName);
    }

    public <T> T map(Object source, Class<T> targetClass, MappingContext context) {
        return map(source, targetClass, defaultMappingName, context);
    }

    @Override
    public <T> T map(Object source, Class<T> targetClass, String mappingName) {
        return map(source, targetClass, mappingName, getEmptyContext());
    }

    protected <T> T map(Object source, Class<T> targetClass, String mappingName, MappingContext context) {
        if (source == null) {
            return null;
        }
        T result;
        try {
            result = targetClass.newInstance();
        } catch (Exception e) {
            throw new MappingException(targetClass + " has no default constructor", e);
        }
        map(source, result, mappingName, context);
        return result;
    }

    @Override
    public <C extends Collection<T>, T> C map(Collection sourceCollection, Class<T> targetClass, C resultCollection) {
        return map(sourceCollection, targetClass, resultCollection, defaultMappingName);
    }

    protected  <C extends Collection<T>, T> C map(Collection sourceCollection, Class<T> targetClass, C resultCollection,
                                                  MappingContext context) {
        return map(sourceCollection, targetClass, resultCollection, defaultMappingName, context);
    }

    @Override
    public <C extends Collection<T>, T> C map(Collection sourceCollection, Class<T> targetClass, C resultCollection,
                                              String mappingName) {
        return map(sourceCollection, targetClass, resultCollection, mappingName, getEmptyContext());
    }

    protected  <C extends Collection<T>, T> C map(Collection sourceCollection, Class<T> targetClass, C resultCollection,
                                              String mappingName, MappingContext context) {
        for (Object source : sourceCollection) {
            resultCollection.add(map(source, targetClass, mappingName, context));
        }
        return resultCollection;
    }

    @Override
    public <S, C extends Map<S, T>, T> C map(Collection<S> sourceCollection, Class<T> targetClass, C resultCollection) {
        return map(sourceCollection, targetClass, resultCollection, getEmptyContext());
    }

    protected <S, C extends Map<S, T>, T> C map(Collection<S> sourceCollection, Class<T> targetClass, C resultCollection,
                                                MappingContext context) {
        return map(sourceCollection, targetClass, resultCollection, defaultMappingName, context) ;
    }

    @Override
    public <S, C extends Map<S, T>, T> C map(Collection<S> sourceCollection, Class<T> targetClass, C resultCollection,
                                             String mappingName) {
        return map(sourceCollection, targetClass, resultCollection, mappingName, getEmptyContext());
    }

    protected <S, C extends Map<S, T>, T> C map(Collection<S> sourceCollection, Class<T> targetClass, C resultCollection,
                                                String mappingName, MappingContext context) {
        for (S source : sourceCollection) {
            resultCollection.put(source, map(source, targetClass, mappingName, context));
        }
        return resultCollection;
    }

    @Override
    public <T> T[] map(Collection sourceCollection, Class<T> targetClass) {
        return map(sourceCollection, targetClass, defaultMappingName);
    }

    protected  <T> T[] map(Collection sourceCollection, Class<T> targetClass, MappingContext context) {
        return map(sourceCollection, targetClass, defaultMappingName, context);
    }

    @Override
    public <T> T[] map(Collection sourceCollection, Class<T> targetClass, String mappingName) {
        return map(sourceCollection, targetClass, mappingName, getEmptyContext());
    }

    @SuppressWarnings("unchecked")
    protected <T> T[] map(Collection sourceCollection, Class<T> targetClass, String mappingName, MappingContext context) {
        T[] result = (T[]) Array.newInstance(targetClass, sourceCollection.size());
        int i = 0;
        for (Object source : sourceCollection) {
            result[i++] = map(source, targetClass, mappingName, context);
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
        private final boolean requiresContext;

        private MappingValue(Object bean, Method method, boolean requiresContext) {
            this.bean = bean;
            this.method = method;
            this.requiresContext = requiresContext;
        }
    }

    /**
     * Much easier and shorter way would be to use JDK Proxy over ObjectMapper, but due to the number of reasons
     * (e.g. dynamic method resolution, no compile-time checks), the change is deferred until really needed.
     */
    private class ObjectMapperClosure implements ObjectMapper {

        private ObjectMapperImpl objectMapper;
        private MappingContext context;

        private ObjectMapperClosure(MappingContext context) {
            this.objectMapper = ObjectMapperImpl.this;
            this.context = context;
        }

        @Override
        public ObjectMapper using(MappingContext context) {
            return new ObjectMapperClosure(context);
        }

        @Override
        public <T> T map(Object source, T target) {
            return objectMapper.map(source, target, context);
        }

        @Override
        public <T> T map(Object source, T target, String mappingName) {
            return objectMapper.map(source, target, mappingName, context);
        }

        @Override
        public <T> T map(Object source, Class<T> targetClass) {
            return objectMapper.map(source, targetClass, context);
        }

        @Override
        public <T> T map(Object source, Class<T> targetClass, String mappingName) {
            return objectMapper.map(source, targetClass, mappingName, context);
        }

        @Override
        public <C extends Collection<T>, T> C map(Collection sourceCollection, Class<T> targetClass, C resultCollection) {
            return objectMapper.map(sourceCollection, targetClass, resultCollection, context);
        }

        @Override
        public <C extends Collection<T>, T> C map(Collection sourceCollection, Class<T> targetClass, C resultCollection,
                                                  String mappingName) {
            return objectMapper.map(sourceCollection, targetClass, resultCollection, mappingName, context);
        }

        @Override
        public <S, C extends Map<S, T>, T> C map(Collection<S> sourceCollection, Class<T> targetClass, C resultCollection) {
            return objectMapper.map(sourceCollection, targetClass, resultCollection, context);
        }

        @Override
        public <S, C extends Map<S, T>, T> C map(Collection<S> sourceCollection, Class<T> targetClass, C resultCollection,
                                                 String mappingName) {
            return objectMapper.map(sourceCollection, targetClass, resultCollection, mappingName, context);
        }

        @Override
        public <T> T[] map(Collection sourceCollection, Class<T> targetClass) {
            return objectMapper.map(sourceCollection, targetClass, context);
        }

        @Override
        public <T> T[] map(Collection sourceCollection, Class<T> targetClass, String mappingName) {
            return objectMapper.map(sourceCollection, targetClass, mappingName, context);
        }
    }
}
