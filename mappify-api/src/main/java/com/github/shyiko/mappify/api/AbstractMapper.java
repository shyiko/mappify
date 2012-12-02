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
package com.github.shyiko.mappify.api;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

/**
 * Convenient partial implementation of {@link Mapper}, which leaves only two methods to be overridden -
 * {@link #map(Object, Object, String, MappingContext)} and {@link #allowsToMap(Class, Class, String)}.
 *
 * @author <a href="mailto:stanley.shyiko@gmail.com">Stanley Shyiko</a>
 */
public abstract class AbstractMapper implements Mapper {

    private boolean enforceMappingContext = true;

    /**
     * @return true if mapping context must be created at the beginning of the map invocation
     * (if not explicitly provided), false otherwise.
     */
    public boolean isEnforceMappingContext() {
        return enforceMappingContext;
    }

    /**
     * @param enforceMappingContext true indicates that mapping context must always be present during the map process,
     * regardless of whether it was passed or is going to be used.
     */
    public void setEnforceMappingContext(boolean enforceMappingContext) {
        this.enforceMappingContext = enforceMappingContext;
    }

    @Override
    public <T> T map(Object source, T target) {
        return map(source, target, getDefaultMappingName(), getDefaultContext());
    }

    @Override
    public <T> T map(Object source, T target, String mappingName) {
        return map(source, target, mappingName, getDefaultContext());
    }

    @Override
    public <T> T map(Object source, T target, MappingContext mappingContext) {
        return map(source, target, getDefaultMappingName(), mappingContext);
    }

    @Override
    public <T> T map(Object source, Class<T> targetClass) {
        return map(source, targetClass, getDefaultMappingName(), getDefaultContext());
    }

    @Override
    public <T> T map(Object source, Class<T> targetClass, String mappingName) {
        return map(source, targetClass, mappingName, getDefaultContext());
    }

    @Override
    public <T> T map(Object source, Class<T> targetClass, MappingContext mappingContext) {
        return map(source, targetClass, getDefaultMappingName(), mappingContext);
    }

    @Override
    public <T> T map(Object source, Class<T> targetClass, String mappingName, MappingContext mappingContext) {
        if (source == null) {
            return null;
        }
        T result;
        try {
            result = targetClass.newInstance();
        } catch (Exception e) {
            throw new MappingException(targetClass + " has no default constructor", e);
        }
        map(source, result, mappingName, mappingContext);
        return result;
    }

    @Override
    public <C extends Collection<T>, T> C map(Collection sourceCollection, Class<T> targetClass, C targetCollection) {
        return map(sourceCollection, targetClass, targetCollection, getDefaultMappingName(), getDefaultContext());
    }

    @Override
    public <C extends Collection<T>, T> C map(
            Collection sourceCollection, Class<T> targetClass, C targetCollection, String mappingName) {
        return map(sourceCollection, targetClass, targetCollection, mappingName, getDefaultContext());
    }

    @Override
    public <C extends Collection<T>, T> C map(
            Collection sourceCollection, Class<T> targetClass, C targetCollection, MappingContext mappingContext) {
        return map(sourceCollection, targetClass, targetCollection, getDefaultMappingName(), mappingContext);
    }

    @Override
    public <C extends Collection<T>, T> C map(
            Collection sourceCollection, Class<T> targetClass, C targetCollection, String mappingName,
            MappingContext mappingContext) {
        assertNotNull(sourceCollection);
        for (Object source : sourceCollection) {
            targetCollection.add(map(source, targetClass, mappingName, mappingContext));
        }
        return targetCollection;
    }

    protected void assertNotNull(Collection sourceCollection) {
        if (sourceCollection == null) {
            throw new MappingException("Source collection must never be NULL");
        }
    }

    @Override
    public <S, C extends Map<S, T>, T> C map(Collection<S> sourceCollection, Class<T> targetClass, C targetMap) {
        return map(sourceCollection, targetClass, targetMap, getDefaultMappingName(), getDefaultContext());
    }

    @Override
    public <S, C extends Map<S, T>, T> C map(
            Collection<S> sourceCollection, Class<T> targetClass, C targetMap, String mappingName) {
        return map(sourceCollection, targetClass, targetMap, mappingName, getDefaultContext());
    }

    @Override
    public <S, C extends Map<S, T>, T> C map(
            Collection<S> sourceCollection, Class<T> targetClass, C targetMap, MappingContext mappingContext) {
        return map(sourceCollection, targetClass, targetMap, getDefaultMappingName(), mappingContext);
    }

    @Override
    public <S, C extends Map<S, T>, T> C map(
            Collection<S> sourceCollection, Class<T> targetClass, C targetMap, String mappingName,
            MappingContext mappingContext) {
        assertNotNull(sourceCollection);
        for (S source : sourceCollection) {
            targetMap.put(source, map(source, targetClass, mappingName, mappingContext));
        }
        return targetMap;
    }

    @Override
    public <T> T[] map(Collection sourceCollection, Class<T> targetClass) {
        return map(sourceCollection, targetClass, getDefaultMappingName(), getDefaultContext());
    }

    @Override
    public <T> T[] map(Collection sourceCollection, Class<T> targetClass, String mappingName) {
        return map(sourceCollection, targetClass, mappingName, getDefaultContext());
    }

    @Override
    public <T> T[] map(Collection sourceCollection, Class<T> targetClass, MappingContext mappingContext) {
        return map(sourceCollection, targetClass, getDefaultMappingName(), mappingContext);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T[] map(
            Collection sourceCollection, Class<T> targetClass, String mappingName, MappingContext mappingContext) {
        assertNotNull(sourceCollection);
        T[] result = (T[]) Array.newInstance(targetClass, sourceCollection.size());
        int i = 0;
        for (Object source : sourceCollection) {
            result[i++] = map(source, targetClass, mappingName, mappingContext);
        }
        return result;
    }

    @Override
    public boolean allowsToMap(Class sourceClass, Class targetClass) {
        return allowsToMap(sourceClass, targetClass, getDefaultMappingName());
    }

    protected String getDefaultMappingName() {
        return "";
    }

    protected MappingContext getDefaultContext() {
        return enforceMappingContext ? new MappingContext() : null;
    }
}
