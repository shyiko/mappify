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
 * Defines contract for object mappings.
 * @author <a href="mailto:stanley.shyiko@gmail.com">Stanley Shyiko</a>
 */
public interface ObjectMapper {

    /**
     * Perform mapping from source to target.
     * @param source source object
     * @param target target object
     */
    <T> void map(Object source, T target);

    /**
     * Perform mapping form source object to target class.
     * @param source source object. nullable
     * @param targetClass target class
     * @return null if source is null, otherwise mapped instance of target class
     */
    <T> T map(Object source, Class<T> targetClass);

    /**
     * Perform mapping of objects provided by sources parameter.
     * @param sources collection of objects
     * @param targetClass target class
     * @return {@link HashSet} containing mapped instances. never null
     */
    <T> HashSet<T> mapToHashSet(Collection sources, Class<T> targetClass);

    /**
     * Perform mapping of objects provided by sources parameter.
     * @param sources collection of objects
     * @param targetClass target class
     * @return {@link TreeSet} containing mapped instances. never null
     */
    <T> TreeSet<T> mapToTreeSet(Collection sources, Class<T> targetClass);

    /**
     * Perform mapping of objects provided by sources parameter.
     * @param sources collection of objects
     * @param targetClass target class
     * @return {@link LinkedList} containing mapped instances. never null
     */
    <T> LinkedList<T> mapToLinkedList(Collection sources, Class<T> targetClass);

    /**
     * Perform mapping of objects provided by sources parameter.
     * @param sources collection of objects
     * @param targetClass target class
     * @return {@link ArrayList} containing mapped instances. never null
     */
    <T> ArrayList<T> mapToArrayList(Collection sources, Class<T> targetClass);

    /**
     * Perform mapping of objects provided by sources parameter.
     * @param sources collection of objects
     * @param targetClass target class
     * @return array containing mapped instances. never null
     */
    <T> T[] mapToArray(Collection sources, Class<T> targetClass);
}
