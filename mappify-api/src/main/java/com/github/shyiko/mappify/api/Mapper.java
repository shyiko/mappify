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

import java.util.*;

/**
 * Contract for the object mapper. Implementations are required to be thread-safe.
 *
 * @author <a href="mailto:stanley.shyiko@gmail.com">Stanley Shyiko</a>
 */
public interface Mapper {

    /**
     * Map the source object onto the target one.
     * @param source source object
     * @param target target object
     * @return target object
     * @throws MappingException in case of failure during the mapping process
     */
    <T> T map(Object source, T target);

    /**
     * Map the source object onto the target one.
     * @param source source object
     * @param target target object
     * @param mappingName mapping name
     * @param <T> return type
     * @return target object
     * @throws MappingException in case of failure during the mapping process
     */
    <T> T map(Object source, T target, String mappingName);

    /**
     * Map the source object onto the target one.
     * @param source source object
     * @param target target object
     * @param mappingContext mapping context
     * @param <T> return type
     * @return target object
     * @throws MappingException in case of failure during the mapping process
     */
    <T> T map(Object source, T target, MappingContext mappingContext);

    /**
     * Map the source object onto the target one.
     * @param source source object
     * @param target target object
     * @param mappingName mapping name
     * @param mappingContext mapping context
     * @param <T> return type
     * @return target object
     * @throws MappingException in case of failure during the mapping process
     */
    <T> T map(Object source, T target, String mappingName, MappingContext mappingContext);

    /**
     * Map the source object into the object of target class.
     * @param source source object. nullable
     * @param targetClass target class
     * @param <T> return type
     * @return target object. nullable
     * @throws MappingException in case of failure during the mapping process
     */
    <T> T map(Object source, Class<T> targetClass);

    /**
     * Map the source object into the object of target class.
     * @param source source object. nullable
     * @param targetClass target class
     * @param mappingName mapping name
     * @param <T> return type
     * @return target object. nullable
     * @throws MappingException in case of failure during the mapping process
     */
    <T> T map(Object source, Class<T> targetClass, String mappingName);

    /**
     * Map the source object into the object of target class.
     * @param source source object. nullable
     * @param targetClass target class
     * @param mappingContext mapping context
     * @param <T> return type
     * @return target object. nullable
     * @throws MappingException in case of failure during the mapping process
     */
    <T> T map(Object source, Class<T> targetClass, MappingContext mappingContext);

    /**
     * Map the source object into the object of target class.
     * @param source source object. nullable
     * @param targetClass target class
     * @param mappingName mapping name
     * @param mappingContext mapping context
     * @param <T> return type
     * @return target object. nullable
     * @throws MappingException in case of failure during the mapping process
     */
    <T> T map(Object source, Class<T> targetClass, String mappingName, MappingContext mappingContext);

    /**
     * Map each element of source collection into corresponding object of target class.
     * Result is added to the target collection.
     * @param sourceCollection source collection
     * @param targetClass target class
     * @param targetCollection target collection
     * @param <C> collection type
     * @param <T> return type
     * @return target collection
     * @throws MappingException in case of failure during the mapping process
     */
    <C extends Collection<T>, T> C map(
            Collection sourceCollection, Class<T> targetClass, C targetCollection);

    /**
     * Map each element of source collection into corresponding object of target class.
     * Result is added to the target collection.
     * @param sourceCollection source collection
     * @param targetClass target class
     * @param targetCollection target collection
     * @param mappingName mapping name
     * @param <C> collection type
     * @param <T> return type
     * @return target collection
     * @throws MappingException in case of failure during the mapping process
     */
    <C extends Collection<T>, T> C map(
            Collection sourceCollection, Class<T> targetClass, C targetCollection, String mappingName);

    /**
     * Map each element of source collection into corresponding object of target class.
     * Result is added to the target collection.
     * @param sourceCollection source collection
     * @param targetClass target class
     * @param targetCollection target collection
     * @param mappingContext mapping context
     * @param <C> collection type
     * @param <T> return type
     * @return target collection
     * @throws MappingException in case of failure during the mapping process
     */
    <C extends Collection<T>, T> C map(
            Collection sourceCollection, Class<T> targetClass, C targetCollection, MappingContext mappingContext);

    /**
     * Map each element of source collection into corresponding object of target class.
     * Result is added to the target collection.
     * @param sourceCollection source collection
     * @param targetClass target class
     * @param targetCollection target collection
     * @param mappingName mapping name
     * @param mappingContext mapping context
     * @param <C> collection type
     * @param <T> return type
     * @return target collection
     * @throws MappingException in case of failure during the mapping process
     */
    <C extends Collection<T>, T> C map(
            Collection sourceCollection, Class<T> targetClass, C targetCollection, String mappingName,
            MappingContext mappingContext);

    /**
     * Map each element of source collection into corresponding object of target class.
     * Result is added to the target map (source object (key) -> target object (value)).
     * @param sourceCollection source collection
     * @param targetClass target class
     * @param targetMap target map
     * @param <S> source type
     * @param <C> map type
     * @param <T> return type
     * @return target map
     * @throws MappingException in case of failure during the mapping process
     */
    <S, C extends Map<S, T>, T> C map(
            Collection<S> sourceCollection, Class<T> targetClass, C targetMap);

    /**
     * Map each element of source collection into corresponding object of target class.
     * Result is added to the target map (source object (key) -> target object (value)).
     * @param sourceCollection source collection
     * @param targetClass target class
     * @param targetMap target map
     * @param mappingName mapping name
     * @param <S> source type
     * @param <C> map type
     * @param <T> return type
     * @return target map
     * @throws MappingException in case of failure during the mapping process
     */
    <S, C extends Map<S, T>, T> C map(
            Collection<S> sourceCollection, Class<T> targetClass, C targetMap, String mappingName);

    /**
     * Map each element of source collection into corresponding object of target class.
     * Result is added to the target map (source object (key) -> target object (value)).
     * @param sourceCollection source collection
     * @param targetClass target class
     * @param targetMap target map
     * @param mappingContext mapping context
     * @param <S> source type
     * @param <C> map type
     * @param <T> return type
     * @return target map
     * @throws MappingException in case of failure during the mapping process
     */
    <S, C extends Map<S, T>, T> C map(
            Collection<S> sourceCollection, Class<T> targetClass, C targetMap, MappingContext mappingContext);

    /**
     * Map each element of source collection into corresponding object of target class.
     * Result is added to the target map (source object (key) -> target object (value)).
     * @param sourceCollection source collection
     * @param targetClass target class
     * @param targetMap target map
     * @param mappingName mapping name
     * @param mappingContext mapping context
     * @param <S> source type
     * @param <C> map type
     * @param <T> return type
     * @return target map
     * @throws MappingException in case of failure during the mapping process
     */
    <S, C extends Map<S, T>, T> C map(
            Collection<S> sourceCollection, Class<T> targetClass, C targetMap, String mappingName,
            MappingContext mappingContext);

    /**
     * Map each element of source collection into corresponding object of target class.
     * Result is returned in for of an array.
     * @param sourceCollection source collection
     * @param targetClass target class
     * @param <T> return type
     * @return array of mapped instances
     * @throws MappingException in case of failure during the mapping process
     */
    <T> T[] map(Collection sourceCollection, Class<T> targetClass);

    /**
     * Map each element of source collection into corresponding object of target class.
     * Result is returned in for of an array.
     * @param sourceCollection source collection
     * @param targetClass target class
     * @param mappingName mapping name
     * @param <T> return type
     * @return array of mapped instances
     * @throws MappingException in case of failure during the mapping process
     */
    <T> T[] map(Collection sourceCollection, Class<T> targetClass, String mappingName);

    /**
     * Map each element of source collection into corresponding object of target class.
     * Result is returned in for of an array.
     * @param sourceCollection source collection
     * @param targetClass target class
     * @param mappingContext mapping context
     * @param <T> return type
     * @return array of mapped instances
     * @throws MappingException in case of failure during the mapping process
     */
    <T> T[] map(Collection sourceCollection, Class<T> targetClass, MappingContext mappingContext);

    /**
     * Map each element of source collection into corresponding object of target class.
     * Result is returned in for of an array.
     * @param sourceCollection source collection
     * @param targetClass target class
     * @param mappingName mapping name
     * @param mappingContext mapping context
     * @param <T> return type
     * @return array of mapped instances
     * @throws MappingException in case of failure during the mapping process
     */
    <T> T[] map(Collection sourceCollection, Class<T> targetClass, String mappingName, MappingContext mappingContext);


    /**
     * Map each element of source array into corresponding object of target class.
     * Result is added to the target collection.
     * @param sourceArray source array
     * @param targetClass target class
     * @param targetCollection target collection
     * @param <S> source type
     * @param <C> collection type
     * @param <T> return type
     * @return target collection
     * @throws MappingException in case of failure during the mapping process
     */
    <S, C extends Collection<T>, T> C map(S[] sourceArray, Class<T> targetClass, C targetCollection);

    /**
     * Map each element of source array into corresponding object of target class.
     * Result is added to the target collection.
     * @param sourceArray source array
     * @param targetClass target class
     * @param targetCollection target collection
     * @param mappingName mapping name
     * @param <S> source type
     * @param <C> collection type
     * @param <T> return type
     * @return target collection
     * @throws MappingException in case of failure during the mapping process
     */
    <S, C extends Collection<T>, T> C map(
            S[] sourceArray, Class<T> targetClass, C targetCollection, String mappingName);

    /**
     * Map each element of source array into corresponding object of target class.
     * Result is added to the target collection.
     * @param sourceArray source array
     * @param targetClass target class
     * @param targetCollection target collection
     * @param mappingContext mapping context
     * @param <S> source type
     * @param <C> collection type
     * @param <T> return type
     * @return target collection
     * @throws MappingException in case of failure during the mapping process
     */
    <S, C extends Collection<T>, T> C map(
            S[] sourceArray, Class<T> targetClass, C targetCollection, MappingContext mappingContext);

    /**
     * Map each element of source array into corresponding object of target class.
     * Result is added to the target collection.
     * @param sourceArray source array
     * @param targetClass target class
     * @param targetCollection target collection
     * @param mappingName mapping name
     * @param mappingContext mapping context
     * @param <S> source type
     * @param <C> collection type
     * @param <T> return type
     * @return target collection
     * @throws MappingException in case of failure during the mapping process
     */
    <S, C extends Collection<T>, T> C map(
            S[] sourceArray, Class<T> targetClass, C targetCollection, String mappingName,
            MappingContext mappingContext);

    /**
     * Map each element of source array into corresponding object of target class.
     * Result is added to the target map (source object (key) -> target object (value)).
     * @param sourceArray source array
     * @param targetClass target class
     * @param targetMap target map
     * @param <S> source type
     * @param <C> map type
     * @param <T> return type
     * @return target map
     * @throws MappingException in case of failure during the mapping process
     */
    <S, C extends Map<S, T>, T> C map(
            S[] sourceArray, Class<T> targetClass, C targetMap);

    /**
     * Map each element of source array into corresponding object of target class.
     * Result is added to the target map (source object (key) -> target object (value)).
     * @param sourceArray source array
     * @param targetClass target class
     * @param targetMap target map
     * @param mappingName mapping name
     * @param <S> source type
     * @param <C> map type
     * @param <T> return type
     * @return target map
     * @throws MappingException in case of failure during the mapping process
     */
    <S, C extends Map<S, T>, T> C map(
            S[] sourceArray, Class<T> targetClass, C targetMap, String mappingName);

    /**
     * Map each element of source array into corresponding object of target class.
     * Result is added to the target map (source object (key) -> target object (value)).
     * @param sourceArray source array
     * @param targetClass target class
     * @param targetMap target map
     * @param mappingContext mapping context
     * @param <S> source type
     * @param <C> map type
     * @param <T> return type
     * @return target map
     * @throws MappingException in case of failure during the mapping process
     */
    <S, C extends Map<S, T>, T> C map(
            S[] sourceArray, Class<T> targetClass, C targetMap, MappingContext mappingContext);

    /**
     * Map each element of source array into corresponding object of target class.
     * Result is added to the target map (source object (key) -> target object (value)).
     * @param sourceArray source array
     * @param targetClass target class
     * @param targetMap target map
     * @param mappingName mapping name
     * @param mappingContext mapping context
     * @param <S> source type
     * @param <C> map type
     * @param <T> return type
     * @return target map
     * @throws MappingException in case of failure during the mapping process
     */
    <S, C extends Map<S, T>, T> C map(
            S[] sourceArray, Class<T> targetClass, C targetMap, String mappingName, MappingContext mappingContext);

    /**
     * Map each element of source array into corresponding object of target class.
     * Result is returned in for of an array.
     * @param sourceArray source array
     * @param targetClass target class
     * @param <S> source type
     * @param <T> return type
     * @return array of mapped instances
     * @throws MappingException in case of failure during the mapping process
     */
    <S, T> T[] map(S[] sourceArray, Class<T> targetClass);

    /**
     * Map each element of source array into corresponding object of target class.
     * Result is returned in for of an array.
     * @param sourceArray source array
     * @param targetClass target class
     * @param mappingName mapping name
     * @param <S> source type
     * @param <T> return type
     * @return array of mapped instances
     * @throws MappingException in case of failure during the mapping process
     */
    <S, T> T[] map(S[] sourceArray, Class<T> targetClass, String mappingName);

    /**
     * Map each element of source array into corresponding object of target class.
     * Result is returned in for of an array.
     * @param sourceArray source array
     * @param targetClass target class
     * @param mappingContext mapping context
     * @param <S> source type
     * @param <T> return type
     * @return array of mapped instances
     * @throws MappingException in case of failure during the mapping process
     */
    <S, T> T[] map(S[] sourceArray, Class<T> targetClass, MappingContext mappingContext);

    /**
     * Map each element of source array into corresponding object of target class.
     * Result is returned in for of an array.
     * @param sourceArray source array
     * @param targetClass target class
     * @param mappingName mapping name
     * @param mappingContext mapping context
     * @param <S> source type
     * @param <T> return type
     * @return array of mapped instances
     * @throws MappingException in case of failure during the mapping process
     */
    <S, T> T[] map(S[] sourceArray, Class<T> targetClass, String mappingName, MappingContext mappingContext);

    /**
     * Determine whether mapper has mapping definition for source class -> target class.
     * @param sourceClass source class
     * @param targetClass target class
     * @return true if mapper is capable of mapping source class to the target, false otherwise
     */
    boolean allowsToMap(Class sourceClass, Class targetClass);

    /**
     * Determine whether mapper has mapping definition for source class -> target class.
     * @param sourceClass source class
     * @param targetClass target class
     * @param mappingName mapping name
     * @return true if mapper is capable of mapping source class to the target, false otherwise
     */
    boolean allowsToMap(Class sourceClass, Class targetClass, String mappingName);

    /**
     * Alias for map(sourceCollection, targetClass, new ArrayList(sourceCollection.size())).
     * @see #map(java.util.Collection, Class, java.util.Collection)
     */
    <T> ArrayList<T> mapToArrayList(Collection sourceCollection, Class<T> targetClass);

    /**
     * Alias for map(sourceCollection, targetClass, new ArrayList(sourceCollection.size()), mappingName).
     * @see #map(java.util.Collection, Class, java.util.Collection, String)
     */
    <T> ArrayList<T> mapToArrayList(Collection sourceCollection, Class<T> targetClass, String mappingName);

    /**
     * Alias for map(sourceCollection, targetClass, new ArrayList(sourceCollection.size()), mappingContext).
     * @see #map(java.util.Collection, Class, java.util.Collection, MappingContext)
     */
    <T> ArrayList<T> mapToArrayList(Collection sourceCollection, Class<T> targetClass, MappingContext mappingContext);

    /**
     * Alias for map(sourceCollection, targetClass, new ArrayList(sourceCollection.size()), mappingName, mappingContext).
     * @see #map(java.util.Collection, Class, java.util.Collection, String, MappingContext)
     */
    <T> ArrayList<T> mapToArrayList(
            Collection sourceCollection, Class<T> targetClass, String mappingName, MappingContext mappingContext);

    /**
     * Alias for map(sourceArray, targetClass, new ArrayList(sourceArray.length)).
     * @see #map(Object[], Class, java.util.Collection)
     */
    <S, T> ArrayList<T> mapToArrayList(S[] sourceArray, Class<T> targetClass);

    /**
     * Alias for map(sourceArray, targetClass, new ArrayList(sourceArray.length), mappingName).
     * @see #map(Object[], Class, java.util.Collection, String)
     */
    <S, T> ArrayList<T> mapToArrayList(S[] sourceArray, Class<T> targetClass, String mappingName);

    /**
     * Alias for map(sourceArray, targetClass, new ArrayList(sourceArray.length), mappingContext).
     * @see #map(Object[], Class, java.util.Collection, MappingContext)
     */
    <S, T> ArrayList<T> mapToArrayList(S[] sourceArray, Class<T> targetClass, MappingContext mappingContext);

    /**
     * Alias for map(sourceArray, targetClass, new ArrayList(sourceArray.length), mappingName, mappingContext).
     * @see #map(Object[], Class, java.util.Collection, String, MappingContext)
     */
    <S, T> ArrayList<T> mapToArrayList(
            S[] sourceArray, Class<T> targetClass, String mappingName, MappingContext mappingContext);

    /**
     * Alias for map(sourceCollection, targetClass, new HashSet(sufficient initial capacity))).
     * @see #map(java.util.Collection, Class, java.util.Collection)
     */
    <T> HashSet<T> mapToHashSet(Collection sourceCollection, Class<T> targetClass);

    /**
     * Alias for map(sourceCollection, targetClass, new HashSet(sufficient initial capacity), mappingName).
     * @see #map(java.util.Collection, Class, java.util.Collection, String)
     */
    <T> HashSet<T> mapToHashSet(Collection sourceCollection, Class<T> targetClass, String mappingName);

    /**
     * Alias for map(sourceCollection, targetClass, new HashSet(sufficient initial capacity), mappingContext).
     * @see #map(java.util.Collection, Class, java.util.Collection, MappingContext)
     */
    <T> HashSet<T> mapToHashSet(Collection sourceCollection, Class<T> targetClass, MappingContext mappingContext);

    /**
     * Alias for map(sourceCollection, targetClass, new HashSet(sufficient initial capacity), mappingName, mappingContext).
     * @see #map(java.util.Collection, Class, java.util.Collection, String, MappingContext)
     */
    <T> HashSet<T> mapToHashSet(
            Collection sourceCollection, Class<T> targetClass, String mappingName, MappingContext mappingContext);

    /**
     * Alias for map(sourceArray, targetClass, new HashSet(sufficient initial capacity)).
     * @see #map(Object[], Class, java.util.Collection)
     */
    <S, T> HashSet<T> mapToHashSet(S[] sourceArray, Class<T> targetClass);

    /**
     * Alias for map(sourceArray, targetClass, new HashSet(sufficient initial capacity), mappingName).
     * @see #map(Object[], Class, java.util.Collection, String)
     */
    <S, T> HashSet<T> mapToHashSet(S[] sourceArray, Class<T> targetClass, String mappingName);

    /**
     * Alias for map(sourceArray, targetClass, new HashSet(sufficient initial capacity), mappingContext).
     * @see #map(Object[], Class, java.util.Collection, MappingContext)
     */
    <S, T> HashSet<T> mapToHashSet(S[] sourceArray, Class<T> targetClass, MappingContext mappingContext);

    /**
     * Alias for map(sourceArray, targetClass, new HashSet(sufficient initial capacity), mappingName, mappingContext).
     * @see #map(Object[], Class, java.util.Collection, String, MappingContext)
     */
    <S, T> HashSet<T> mapToHashSet(
            S[] sourceArray, Class<T> targetClass, String mappingName, MappingContext mappingContext);

    /**
     * Alias for map(sourceCollection, targetClass, new LinkedHashSet(sufficient initial capacity))).
     * @see #map(java.util.Collection, Class, java.util.Collection)
     */
    <T> LinkedHashSet<T> mapToLinkedHashSet(Collection sourceCollection, Class<T> targetClass);

    /**
     * Alias for map(sourceCollection, targetClass, new LinkedHashSet(sufficient initial capacity), mappingName).
     * @see #map(java.util.Collection, Class, java.util.Collection, String)
     */
    <T> LinkedHashSet<T> mapToLinkedHashSet(Collection sourceCollection, Class<T> targetClass, String mappingName);

    /**
     * Alias for map(sourceCollection, targetClass, new LinkedHashSet(sufficient initial capacity), mappingContext).
     * @see #map(java.util.Collection, Class, java.util.Collection, MappingContext)
     */
    <T> LinkedHashSet<T> mapToLinkedHashSet(
            Collection sourceCollection, Class<T> targetClass, MappingContext mappingContext);

    /**
     * Alias for map(sourceCollection, targetClass, new LinkedHashSet(sufficient initial capacity), mappingName, mappingContext).
     * @see #map(java.util.Collection, Class, java.util.Collection, String, MappingContext)
     */
    <T> LinkedHashSet<T> mapToLinkedHashSet(
            Collection sourceCollection, Class<T> targetClass, String mappingName, MappingContext mappingContext);

    /**
     * Alias for map(sourceArray, targetClass, new LinkedHashSet(sufficient initial capacity)).
     * @see #map(Object[], Class, java.util.Collection)
     */
    <S, T> LinkedHashSet<T> mapToLinkedHashSet(S[] sourceArray, Class<T> targetClass);

    /**
     * Alias for map(sourceArray, targetClass, new LinkedHashSet(sufficient initial capacity), mappingName).
     * @see #map(Object[], Class, java.util.Collection, String)
     */
    <S, T> LinkedHashSet<T> mapToLinkedHashSet(S[] sourceArray, Class<T> targetClass, String mappingName);

    /**
     * Alias for map(sourceArray, targetClass, new LinkedHashSet(sufficient initial capacity), mappingContext).
     * @see #map(Object[], Class, java.util.Collection, MappingContext)
     */
    <S, T> LinkedHashSet<T> mapToLinkedHashSet(S[] sourceArray, Class<T> targetClass, MappingContext mappingContext);

    /**
     * Alias for map(sourceArray, targetClass, new LinkedHashSet(sufficient initial capacity), mappingName, mappingContext).
     * @see #map(Object[], Class, java.util.Collection, String, MappingContext)
     */
    <S, T> LinkedHashSet<T> mapToLinkedHashSet(
            S[] sourceArray, Class<T> targetClass, String mappingName, MappingContext mappingContext);

    /**
     * Alias for map(sourceCollection, targetClass, new HashMap(sufficient initial capacity))).
     * @see #map(java.util.Collection, Class, java.util.Map)
     */
    <S, T> HashMap<S, T> mapToHashMap(Collection<S> sourceCollection, Class<T> targetClass);

    /**
     * Alias for map(sourceCollection, targetClass, new HashMap(sufficient initial capacity), mappingName).
     * @see #map(java.util.Collection, Class, java.util.Map, String)
     */
    <S, T> HashMap<S, T> mapToHashMap(Collection<S> sourceCollection, Class<T> targetClass, String mappingName);

    /**
     * Alias for map(sourceCollection, targetClass, new HashMap(sufficient initial capacity), mappingContext).
     * @see #map(java.util.Collection, Class, java.util.Map, MappingContext)
     */
    <S, T> HashMap<S, T> mapToHashMap(
            Collection<S> sourceCollection, Class<T> targetClass, MappingContext mappingContext);

    /**
     * Alias for map(sourceCollection, targetClass, new HashMap(sufficient initial capacity), mappingName, mappingContext).
     * @see #map(java.util.Collection, Class, java.util.Map, String, MappingContext)
     */
    <S, T> HashMap<S, T> mapToHashMap(
            Collection<S> sourceCollection, Class<T> targetClass, String mappingName, MappingContext mappingContext);

    /**
     * Alias for map(sourceArray, targetClass, new HashMap(sufficient initial capacity)).
     * @see #map(Object[], Class, java.util.Map)
     */
    <S, T> HashMap<S, T> mapToHashMap(S[] sourceArray, Class<T> targetClass);

    /**
     * Alias for map(sourceArray, targetClass, new HashMap(sufficient initial capacity), mappingName).
     * @see #map(Object[], Class, java.util.Map, String)
     */
    <S, T> HashMap<S, T> mapToHashMap(S[] sourceArray, Class<T> targetClass, String mappingName);

    /**
     * Alias for map(sourceArray, targetClass, new HashMap(sufficient initial capacity), mappingContext).
     * @see #map(Object[], Class, java.util.Map, MappingContext)
     */
    <S, T> HashMap<S, T> mapToHashMap(S[] sourceArray, Class<T> targetClass, MappingContext mappingContext);

    /**
     * Alias for map(sourceArray, targetClass, new HashMap(sufficient initial capacity), mappingName, mappingContext).
     * @see #map(Object[], Class, java.util.Map, String, MappingContext)
     */
    <S, T> HashMap<S, T> mapToHashMap(
            S[] sourceArray, Class<T> targetClass, String mappingName, MappingContext mappingContext);

    /**
     * Alias for map(sourceCollection, targetClass, new LinkedHashMap(sufficient initial capacity))).
     * @see #map(java.util.Collection, Class, java.util.Map)
     */
    <S, T> LinkedHashMap<S, T> mapToLinkedHashMap(Collection<S> sourceCollection, Class<T> targetClass);

    /**
     * Alias for map(sourceCollection, targetClass, new LinkedHashMap(sufficient initial capacity), mappingName).
     * @see #map(java.util.Collection, Class, java.util.Map, String)
     */
    <S, T> LinkedHashMap<S, T> mapToLinkedHashMap(
            Collection<S> sourceCollection, Class<T> targetClass, String mappingName);

    /**
     * Alias for map(sourceCollection, targetClass, new LinkedHashMap(sufficient initial capacity), mappingContext).
     * @see #map(java.util.Collection, Class, java.util.Map, MappingContext)
     */
    <S, T> LinkedHashMap<S, T> mapToLinkedHashMap(
            Collection<S> sourceCollection, Class<T> targetClass, MappingContext mappingContext);

    /**
     * Alias for map(sourceCollection, targetClass, new LinkedHashMap(sufficient initial capacity), mappingName, mappingContext).
     * @see #map(java.util.Collection, Class, java.util.Map, String, MappingContext)
     */
    <S, T> LinkedHashMap<S, T> mapToLinkedHashMap(
            Collection<S> sourceCollection, Class<T> targetClass, String mappingName, MappingContext mappingContext);

    /**
     * Alias for map(sourceArray, targetClass, new LinkedHashMap(sufficient initial capacity)).
     * @see #map(Object[], Class, java.util.Map)
     */
    <S, T> LinkedHashMap<S, T> mapToLinkedHashMap(S[] sourceArray, Class<T> targetClass);

    /**
     * Alias for map(sourceArray, targetClass, new LinkedHashMap(sufficient initial capacity), mappingName).
     * @see #map(Object[], Class, java.util.Map, String)
     */
    <S, T> LinkedHashMap<S, T> mapToLinkedHashMap(S[] sourceArray, Class<T> targetClass, String mappingName);

    /**
     * Alias for map(sourceArray, targetClass, new LinkedHashMap(sufficient initial capacity), mappingContext).
     * @see #map(Object[], Class, java.util.Map, MappingContext)
     */
    <S, T> LinkedHashMap<S, T> mapToLinkedHashMap(S[] sourceArray, Class<T> targetClass, MappingContext mappingContext);

    /**
     * Alias for map(sourceArray, targetClass, new LinkedHashMap(sufficient initial capacity), mappingName, mappingContext).
     * @see #map(Object[], Class, java.util.Map, String, MappingContext)
     */
    <S, T> LinkedHashMap<S, T> mapToLinkedHashMap(
            S[] sourceArray, Class<T> targetClass, String mappingName, MappingContext mappingContext);
}
