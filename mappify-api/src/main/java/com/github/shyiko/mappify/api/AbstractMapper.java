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
import java.util.*;

/**
 * Convenient partial implementation of {@link Mapper}, which leaves only two methods to be overridden -
 * {@link #map(Object, Object, String, MappingContext)} and {@link #allowsToMap(Class, Class, String)}.
 *
 * @author <a href="mailto:stanley.shyiko@gmail.com">Stanley Shyiko</a>
 */
public abstract class AbstractMapper implements Mapper {

    protected volatile boolean enforceMappingContext = true;

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
        assertNotNull(targetClass, "Target class cannot be null");
        return map(source, newInstance(targetClass), mappingName, mappingContext);
    }

    protected <T> T newInstance(Class<T> targetClass) {
        T result;
        try {
            result = targetClass.newInstance();
        } catch (Exception e) {
            throw new MappingException("Class" + targetClass + " has no default constructor", e);
        }
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
        assertNotNull(sourceCollection, "Source collection must never be null");
        for (Object source : sourceCollection) {
            targetCollection.add(map(source, targetClass, mappingName, mappingContext));
        }
        return targetCollection;
    }

    @Override
    public <S, C extends Collection<T>, T> C map(S[] sourceArray, Class<T> targetClass, C targetCollection) {
        return map(sourceArray, targetClass, targetCollection, getDefaultMappingName(), getDefaultContext());
    }

    @Override
    public <S, C extends Collection<T>, T> C map(S[] sourceArray, Class<T> targetClass, C targetCollection,
                                                 String mappingName) {
        return map(sourceArray, targetClass, targetCollection, mappingName, getDefaultContext());
    }

    @Override
    public <S, C extends Collection<T>, T> C map(S[] sourceArray, Class<T> targetClass, C targetCollection,
                                                 MappingContext mappingContext) {
        return map(sourceArray, targetClass, targetCollection, getDefaultMappingName(), getDefaultContext());
    }

    @Override
    public <S, C extends Collection<T>, T> C map(S[] sourceArray, Class<T> targetClass, C targetCollection,
                                                 String mappingName, MappingContext mappingContext) {
        assertNotNull(sourceArray, "Source array must never be null");
        for (Object source : sourceArray) {
            targetCollection.add(map(source, targetClass, mappingName, mappingContext));
        }
        return targetCollection;
    }

    @Override
    public <S, C extends Map<S, T>, T> C map(S[] sourceArray, Class<T> targetClass, C targetMap) {
        return map(sourceArray, targetClass, targetMap, getDefaultMappingName(), getDefaultContext());
    }

    @Override
    public <S, C extends Map<S, T>, T> C map(S[] sourceArray, Class<T> targetClass, C targetMap, String mappingName) {
        return map(sourceArray, targetClass, targetMap, mappingName, getDefaultContext());
    }

    @Override
    public <S, C extends Map<S, T>, T> C map(S[] sourceArray, Class<T> targetClass, C targetMap,
                                             MappingContext mappingContext) {
        return map(sourceArray, targetClass, targetMap, getDefaultMappingName(), mappingContext);
    }

    @Override
    public <S, C extends Map<S, T>, T> C map(S[] sourceArray, Class<T> targetClass, C targetMap, String mappingName,
                                             MappingContext mappingContext) {
        assertNotNull(sourceArray, "Source array must never be null");
        for (S source : sourceArray) {
            targetMap.put(source, map(source, targetClass, mappingName, mappingContext));
        }
        return targetMap;
    }

    @Override
    public <S, T> T[] map(S[] sourceArray, Class<T> targetClass) {
        return map(sourceArray, targetClass, getDefaultMappingName(), getDefaultContext());
    }

    @Override
    public <S, T> T[] map(S[] sourceArray, Class<T> targetClass, String mappingName) {
        return map(sourceArray, targetClass, mappingName, getDefaultContext());
    }

    @Override
    public <S, T> T[] map(S[] sourceArray, Class<T> targetClass, MappingContext mappingContext) {
        return map(sourceArray, targetClass, getDefaultMappingName(), mappingContext);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <S, T> T[] map(S[] sourceArray, Class<T> targetClass, String mappingName, MappingContext mappingContext) {
        assertNotNull(sourceArray, "Source array must never be null");
        T[] result = (T[]) Array.newInstance(targetClass, sourceArray.length);
        int i = 0;
        for (Object source : sourceArray) {
            result[i++] = map(source, targetClass, mappingName, mappingContext);
        }
        return result;
    }

    protected void assertNotNull(Object object, String exceptionMessage) {
        if (object == null) {
            throw new MappingException(exceptionMessage);
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
        assertNotNull(sourceCollection, "Source collection must never be null");
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
        assertNotNull(sourceCollection, "Source collection must never be null");
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

    @Override
    public <T> ArrayList<T> mapToArrayList(Collection sourceCollection, Class<T> targetClass) {
        return map(sourceCollection, targetClass, new ArrayList<T>(determineSICForArrayList(sourceCollection)));
    }

    @Override
    public <T> ArrayList<T> mapToArrayList(Collection sourceCollection, Class<T> targetClass, String mappingName) {
        return map(sourceCollection, targetClass, new ArrayList<T>(determineSICForArrayList(sourceCollection)),
                mappingName);
    }

    @Override
    public <T> ArrayList<T> mapToArrayList(Collection sourceCollection, Class<T> targetClass,
                                           MappingContext mappingContext) {
        return map(sourceCollection, targetClass, new ArrayList<T>(determineSICForArrayList(sourceCollection)),
                mappingContext);
    }

    @Override
    public <T> ArrayList<T> mapToArrayList(Collection sourceCollection, Class<T> targetClass,
                                           String mappingName, MappingContext mappingContext) {
        return map(sourceCollection, targetClass, new ArrayList<T>(determineSICForArrayList(sourceCollection)),
                mappingName, mappingContext);
    }

    @Override
    public <S, T> ArrayList<T> mapToArrayList(S[] sourceArray, Class<T> targetClass) {
        return map(sourceArray, targetClass, new ArrayList<T>(determineSICForArrayList(sourceArray)));
    }

    @Override
    public <S, T> ArrayList<T> mapToArrayList(S[] sourceArray, Class<T> targetClass, String mappingName) {
        return map(sourceArray, targetClass, new ArrayList<T>(determineSICForArrayList(sourceArray)), mappingName);
    }

    @Override
    public <S, T> ArrayList<T> mapToArrayList(S[] sourceArray, Class<T> targetClass, MappingContext mappingContext) {
        return map(sourceArray, targetClass, new ArrayList<T>(determineSICForArrayList(sourceArray)), mappingContext);
    }

    @Override
    public <S, T> ArrayList<T> mapToArrayList(S[] sourceArray, Class<T> targetClass, String mappingName,
                                              MappingContext mappingContext) {
        return map(sourceArray, targetClass, new ArrayList<T>(determineSICForArrayList(sourceArray)),
                mappingName, mappingContext);
    }

    protected int determineSICForArrayList(Collection collection) {
        assertNotNull(collection, "Source collection must never be null");
        return collection.size();
    }

    protected int determineSICForArrayList(Object[] array) {
        assertNotNull(array, "Source array must never be null");
        return array.length;
    }

    @Override
    public <T> HashSet<T> mapToHashSet(Collection sourceCollection, Class<T> targetClass) {
        return map(sourceCollection, targetClass,
                new HashSet<T>(determineSICForMap(sourceCollection)));
    }

    @Override
    public <T> HashSet<T> mapToHashSet(Collection sourceCollection, Class<T> targetClass, String mappingName) {
        return map(sourceCollection, targetClass,
                new HashSet<T>(determineSICForMap(sourceCollection)), mappingName);
    }

    @Override
    public <T> HashSet<T> mapToHashSet(Collection sourceCollection, Class<T> targetClass,
                                       MappingContext mappingContext) {
        return map(sourceCollection, targetClass, new HashSet<T>(determineSICForMap(sourceCollection)),
                mappingContext);
    }

    @Override
    public <T> HashSet<T> mapToHashSet(Collection sourceCollection, Class<T> targetClass, String mappingName,
                                       MappingContext mappingContext) {
        return map(sourceCollection, targetClass, new HashSet<T>(determineSICForMap(sourceCollection)), mappingName,
                mappingContext);
    }

    @Override
    public <S, T> HashSet<T> mapToHashSet(S[] sourceArray, Class<T> targetClass) {
        return map(sourceArray, targetClass, new HashSet<T>(determineSICForMap(sourceArray)));
    }

    @Override
    public <S, T> HashSet<T> mapToHashSet(S[] sourceArray, Class<T> targetClass, String mappingName) {
        return map(sourceArray, targetClass, new HashSet<T>(determineSICForMap(sourceArray)), mappingName);
    }

    @Override
    public <S, T> HashSet<T> mapToHashSet(S[] sourceArray, Class<T> targetClass, MappingContext mappingContext) {
        return map(sourceArray, targetClass, new HashSet<T>(determineSICForMap(sourceArray)), mappingContext);
    }

    @Override
    public <S, T> HashSet<T> mapToHashSet(S[] sourceArray, Class<T> targetClass, String mappingName,
                                          MappingContext mappingContext) {
        return map(sourceArray, targetClass, new HashSet<T>(determineSICForMap(sourceArray)), mappingName,
                mappingContext);
    }

    @Override
    public <T> LinkedHashSet<T> mapToLinkedHashSet(Collection sourceCollection, Class<T> targetClass) {
        return map(sourceCollection, targetClass, new LinkedHashSet<T>(determineSICForMap(sourceCollection)));
    }

    @Override
    public <T> LinkedHashSet<T> mapToLinkedHashSet(Collection sourceCollection, Class<T> targetClass,
                                                   String mappingName) {
        return map(sourceCollection, targetClass, new LinkedHashSet<T>(determineSICForMap(sourceCollection)),
                mappingName);
    }

    @Override
    public <T> LinkedHashSet<T> mapToLinkedHashSet(Collection sourceCollection, Class<T> targetClass, MappingContext mappingContext) {
        return map(sourceCollection, targetClass, new LinkedHashSet<T>(determineSICForMap(sourceCollection)),
                mappingContext);
    }

    @Override
    public <T> LinkedHashSet<T> mapToLinkedHashSet(Collection sourceCollection, Class<T> targetClass,
                                                   String mappingName, MappingContext mappingContext) {
        return map(sourceCollection, targetClass, new LinkedHashSet<T>(determineSICForMap(sourceCollection)),
                mappingName, mappingContext);
    }

    @Override
    public <S, T> LinkedHashSet<T> mapToLinkedHashSet(S[] sourceArray, Class<T> targetClass) {
        return map(sourceArray, targetClass, new LinkedHashSet<T>(determineSICForMap(sourceArray)));
    }

    @Override
    public <S, T> LinkedHashSet<T> mapToLinkedHashSet(S[] sourceArray, Class<T> targetClass, String mappingName) {
        return map(sourceArray, targetClass, new LinkedHashSet<T>(determineSICForMap(sourceArray)), mappingName);
    }

    @Override
    public <S, T> LinkedHashSet<T> mapToLinkedHashSet(S[] sourceArray, Class<T> targetClass,
                                                      MappingContext mappingContext) {
        return map(sourceArray, targetClass, new LinkedHashSet<T>(determineSICForMap(sourceArray)), mappingContext);
    }

    @Override
    public <S, T> LinkedHashSet<T> mapToLinkedHashSet(S[] sourceArray, Class<T> targetClass,
                                                      String mappingName, MappingContext mappingContext) {
        return map(sourceArray, targetClass, new LinkedHashSet<T>(determineSICForMap(sourceArray)), mappingName,
                mappingContext);
    }

    @Override
    public <S, T> HashMap<S, T> mapToHashMap(Collection<S> sourceCollection, Class<T> targetClass) {
        return map(sourceCollection, targetClass, new HashMap<S, T>(determineSICForMap(sourceCollection)));
    }

    @Override
    public <S, T> HashMap<S, T> mapToHashMap(Collection<S> sourceCollection, Class<T> targetClass,
                                             String mappingName) {
        return map(sourceCollection, targetClass, new HashMap<S, T>(determineSICForMap(sourceCollection)),
                mappingName);
    }

    @Override
    public <S, T> HashMap<S, T> mapToHashMap(Collection<S> sourceCollection, Class<T> targetClass,
                                             MappingContext mappingContext) {
        return map(sourceCollection, targetClass, new HashMap<S, T>(determineSICForMap(sourceCollection)),
                mappingContext);
    }

    @Override
    public <S, T> HashMap<S, T> mapToHashMap(Collection<S> sourceCollection, Class<T> targetClass,
                                             String mappingName, MappingContext mappingContext) {
        return map(sourceCollection, targetClass, new HashMap<S, T>(determineSICForMap(sourceCollection)),
                mappingName, mappingContext);
    }

    @Override
    public <S, T> HashMap<S, T> mapToHashMap(S[] sourceArray, Class<T> targetClass) {
        return map(sourceArray, targetClass, new HashMap<S, T>(determineSICForMap(sourceArray)));
    }

    @Override
    public <S, T> HashMap<S, T> mapToHashMap(S[] sourceArray, Class<T> targetClass, String mappingName) {
        return map(sourceArray, targetClass, new HashMap<S, T>(determineSICForMap(sourceArray)), mappingName);
    }

    @Override
    public <S, T> HashMap<S, T> mapToHashMap(S[] sourceArray, Class<T> targetClass, MappingContext mappingContext) {
        return map(sourceArray, targetClass, new HashMap<S, T>(determineSICForMap(sourceArray)), mappingContext);
    }

    @Override
    public <S, T> HashMap<S, T> mapToHashMap(S[] sourceArray, Class<T> targetClass, String mappingName,
                                             MappingContext mappingContext) {
        return map(sourceArray, targetClass, new HashMap<S, T>(determineSICForMap(sourceArray)),
                mappingName, mappingContext);
    }

    @Override
    public <S, T> LinkedHashMap<S, T> mapToLinkedHashMap(Collection<S> sourceCollection, Class<T> targetClass) {
        return map(sourceCollection, targetClass, new LinkedHashMap<S, T>(determineSICForMap(sourceCollection)));
    }

    @Override
    public <S, T> LinkedHashMap<S, T> mapToLinkedHashMap(Collection<S> sourceCollection, Class<T> targetClass,
                                                         String mappingName) {
        return map(sourceCollection, targetClass, new LinkedHashMap<S, T>(determineSICForMap(sourceCollection)),
                mappingName);
    }

    @Override
    public <S, T> LinkedHashMap<S, T> mapToLinkedHashMap(Collection<S> sourceCollection, Class<T> targetClass,
                                                         MappingContext mappingContext) {
        return map(sourceCollection, targetClass, new LinkedHashMap<S, T>(determineSICForMap(sourceCollection)),
                mappingContext);
    }

    @Override
    public <S, T> LinkedHashMap<S, T> mapToLinkedHashMap(Collection<S> sourceCollection, Class<T> targetClass,
                                                         String mappingName, MappingContext mappingContext) {
        return map(sourceCollection, targetClass, new LinkedHashMap<S, T>(determineSICForMap(sourceCollection)),
                mappingName, mappingContext);
    }

    @Override
    public <S, T> LinkedHashMap<S, T> mapToLinkedHashMap(S[] sourceArray, Class<T> targetClass) {
        return map(sourceArray, targetClass, new LinkedHashMap<S, T>(determineSICForMap(sourceArray)));
    }

    @Override
    public <S, T> LinkedHashMap<S, T> mapToLinkedHashMap(S[] sourceArray, Class<T> targetClass, String mappingName) {
        return map(sourceArray, targetClass, new LinkedHashMap<S, T>(determineSICForMap(sourceArray)), mappingName);
    }

    @Override
    public <S, T> LinkedHashMap<S, T> mapToLinkedHashMap(S[] sourceArray, Class<T> targetClass,
                                                         MappingContext mappingContext) {
        return map(sourceArray, targetClass, new LinkedHashMap<S, T>(determineSICForMap(sourceArray)), mappingContext);
    }

    @Override
    public <S, T> LinkedHashMap<S, T> mapToLinkedHashMap(S[] sourceArray, Class<T> targetClass,
                                                         String mappingName, MappingContext mappingContext) {
        return map(sourceArray, targetClass, new LinkedHashMap<S, T>(determineSICForMap(sourceArray)),
                mappingName, mappingContext);
    }

    protected int determineSICForMap(Collection collection) {
        assertNotNull(collection, "Source collection must never be null");
        return determineSufficientInitialCapacityForMap(collection.size());
    }

    protected int determineSICForMap(Object[] array) {
        assertNotNull(array, "Source array must never be null");
        return determineSufficientInitialCapacityForMap(array.length);
    }

    protected int determineSufficientInitialCapacityForMap(int size) {
        return Math.max((int) (size / .75f) + 1, 16);
    }

    protected String getDefaultMappingName() {
        return "";
    }

    protected MappingContext getDefaultContext() {
        return enforceMappingContext ? new MappingContext() : null;
    }
}
