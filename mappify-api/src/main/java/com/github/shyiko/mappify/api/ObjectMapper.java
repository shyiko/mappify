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

import java.util.Collection;

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
     * @param sources list of objects
     * @param targetClass target class
     * @return list of mapped instances of target class. never null
     */
    <T> Collection<T> map(Collection sources, Class<T> targetClass);

    /**
     * Perform mapping of objects provided by sources parameter.
     * @param sources list of objects
     * @param targetClass target class
     * @return array of mapped instances of target class. never null
     */
    <T> T[] mapToArray(Collection sources, Class<T> targetClass);
}
