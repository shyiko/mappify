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
 * Contract for the object mapper.
 * @author <a href="mailto:stanley.shyiko@gmail.com">Stanley Shyiko</a>
 */
public interface ObjectMapper {

    /**
     * Overlay current mapping context with a custom one. Note that context is bound only to the object being returned.
     * That is, objectMapper and objectMapper.using(...) will have different mapping contexts.
     * @param context mapping context
     * @return object mapper bound to the provided mapping context
     */
    ObjectMapper using(MappingContext context);

    /**
     * Map the source object onto the target one.
     * @param source source object
     * @param target target object
     * @return target object
     * @throws MappingException in case of failure during the mapping process
     */
    <T> T map(Object source, T target);

    /**
     * Map the source object onto the target one using a custom mapping.
     * @param source source object
     * @param target target object
     * @param mappingName mapping name
     * @param <T> return type
     * @return target object
     * @throws MappingException in case of failure during the mapping process
     */
    <T> T map(Object source, T target, String mappingName);

    /**
     * Perform mapping form the source object to the target class.
     * @param source source object. nullable
     * @param targetClass target class
     * @param <T> return type
     * @return null if source is null, otherwise mapped instance of the target class
     * @throws MappingException in case of failure during the mapping process
     */
    <T> T map(Object source, Class<T> targetClass);

    /**
     * Perform mapping form the source object to the target class using a custom mapping.
     * @param source source object. nullable
     * @param targetClass target class
     * @param mappingName mapping name
     * @param <T> return type
     * @return null if source is null, otherwise mapped instance of the target class
     * @throws MappingException in case of failure during the mapping process
     */
    <T> T map(Object source, Class<T> targetClass, String mappingName);

    /**
     * Map the objects and add the result to the requested collection.
     * @param sourceCollection collection of objects to map
     * @param targetClass target class
     * @param resultCollection collection to hold the result
     * @param <C> collection type
     * @param <T> return type
     * @return collection of mapped objects
     * @throws MappingException in case of failure during the mapping process
     */
    <C extends Collection<T>, T> C map(Collection sourceCollection, Class<T> targetClass, C resultCollection);

    /**
     * Map the objects using a custom mapping and add the result to the requested collection.
     * @param sourceCollection collection of objects to map
     * @param targetClass target class
     * @param resultCollection collection to hold the result
     * @param mappingName mapping name
     * @param <C> collection type
     * @param <T> return type
     * @return collection of mapped objects
     * @throws MappingException in case of failure during the mapping process
     */
    <C extends Collection<T>, T> C map(Collection sourceCollection, Class<T> targetClass, C resultCollection, String mappingName);

    /**
     * Map the objects and add the result to the requested map.
     * @param sourceCollection collection of objects to map
     * @param targetClass target class
     * @param resultCollection collection to hold the result
     * @param <S> source type
     * @param <C> collection type
     * @param <T> return type
     * @return collection of mapped objects
     * @throws MappingException in case of failure during the mapping process
     */
    <S, C extends Map<S, T>, T> C map(Collection<S> sourceCollection, Class<T> targetClass, C resultCollection);

    /**
     * Map the objects using a custom mapping and add the result to the requested map.
     * @param sourceCollection collection of objects to map
     * @param targetClass target class
     * @param resultCollection collection to hold the result
     * @param mappingName mapping name
     * @param <S> source type
     * @param <C> collection type
     * @param <T> return type
     * @return collection of mapped objects
     * @throws MappingException in case of failure during the mapping process
     */
    <S, C extends Map<S, T>, T> C map(Collection<S> sourceCollection, Class<T> targetClass, C resultCollection, String mappingName);

    /**
     * Map the objects and return the result in form of an array.
     * @param sourceCollection collection of objects to map
     * @param targetClass target class
     * @param <T> return type
     * @return array containing mapped instances. never null
     * @throws MappingException in case of failure during the mapping process
     */
    <T> T[] map(Collection sourceCollection, Class<T> targetClass);

    /**
     * Map the objects using a custom mapping and return the result in form of an array.
     * @param sourceCollection collection of objects to map
     * @param targetClass target class
     * @param mappingName mapping name
     * @param <T> return type
     * @return array containing mapped instances. never null
     * @throws MappingException in case of failure during the mapping process
     */
    <T> T[] map(Collection sourceCollection, Class<T> targetClass, String mappingName);
}
