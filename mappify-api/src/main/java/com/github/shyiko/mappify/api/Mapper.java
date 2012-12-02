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
import java.util.Map;

/**
 * Contract for the object mapper.
 *
 * @author <a href="mailto:stanley.shyiko@gmail.com">Stanley Shyiko</a>
 */
public interface Mapper {

    <T> T map(Object source, T target);

    <T> T map(Object source, T target, String mappingName);

    <T> T map(Object source, T target, MappingContext mappingContext);

    <T> T map(Object source, T target, String mappingName, MappingContext mappingContext);

    <T> T map(Object source, Class<T> targetClass);

    <T> T map(Object source, Class<T> targetClass, String mappingName);

    <T> T map(Object source, Class<T> targetClass, MappingContext mappingContext);

    <T> T map(Object source, Class<T> targetClass, String mappingName, MappingContext mappingContext);

    <C extends Collection<T>, T> C map(
            Collection sourceCollection, Class<T> targetClass, C targetCollection);

    <C extends Collection<T>, T> C map(
            Collection sourceCollection, Class<T> targetClass, C targetCollection, String mappingName);

    <C extends Collection<T>, T> C map(
            Collection sourceCollection, Class<T> targetClass, C targetCollection, MappingContext mappingContext);

    <C extends Collection<T>, T> C map(
            Collection sourceCollection, Class<T> targetClass, C targetCollection, String mappingName,
            MappingContext mappingContext);

    <S, C extends Map<S, T>, T> C map(
            Collection<S> sourceCollection, Class<T> targetClass, C targetMap);

    <S, C extends Map<S, T>, T> C map(
            Collection<S> sourceCollection, Class<T> targetClass, C targetMap, String mappingName);

    <S, C extends Map<S, T>, T> C map(
            Collection<S> sourceCollection, Class<T> targetClass, C targetMap, MappingContext mappingContext);

    <S, C extends Map<S, T>, T> C map(
            Collection<S> sourceCollection, Class<T> targetClass, C targetMap, String mappingName,
            MappingContext mappingContext);

    <T> T[] map(Collection sourceCollection, Class<T> targetClass);

    <T> T[] map(Collection sourceCollection, Class<T> targetClass, String mappingName);

    <T> T[] map(Collection sourceCollection, Class<T> targetClass, MappingContext mappingContext);

    <T> T[] map(Collection sourceCollection, Class<T> targetClass, String mappingName, MappingContext mappingContext);

    boolean allowsToMap(Class sourceClass, Class targetClass);

    boolean allowsToMap(Class sourceClass, Class targetClass, String mappingName);
}
