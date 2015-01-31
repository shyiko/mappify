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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Mapping context which is available to the mappers during the mapping process.
 * Implementation is not thread-safe. Thus, same instance of this class should not be shared between multiple
 * threads.
 *
 * @author <a href="mailto:stanley.shyiko@gmail.com">Stanley Shyiko</a>
 */
public class MappingContext {

    protected Map<String, Object> context;
    protected Iterable source;
    protected int sourceIndex = -1;

    public MappingContext() {
        this((Map<String, Object>) null);
    }

    /**
     * @param context mapping context to copy mappings from
     */
    public MappingContext(MappingContext context) {
        this(context.context);
    }

    /**
     * @param data data
     */
    public MappingContext(Map<String, Object> data) {
        context = initContext(data);
    }

    /**
     * Same as
     * <pre>
     * Map&lt;String, Object&gt; data = new HashMap&lt;String, Object&gt;();
     * data.put(key, value);
     * ... = new MappingContext(data)
     * </pre>
     * @param key key
     * @param value any object that needs to be available during the mapping process
     */
    public MappingContext(String key, Object value) {
        this((Map<String, Object>) null);
        context.put(key, value);
    }

    /**
     * @param key key
     * @param <T> return type
     * @return object by the given key. nullable
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return (T) context.get(key);
    }

    /**
     * @param key key
     * @param defaultValue default value
     * @param <T> return type
     * @return defaultValue if there is no mapping for the given key (or mapping value is null), otherwise -
     * associated value
     */
    public <T> T get(String key, T defaultValue) {
        T result = get(key);
        return result == null ? defaultValue : result;
    }

    /**
     * @deprecated use {@link #containsKey(String)} instead
     * @param key key
     * @return true if this context contains a mapping for the given key
     */
    @Deprecated
    public boolean hasKey(String key) {
        return containsKey(key);
    }

    /**
     * @param key key
     * @return true if this context contains a mapping for the given key
     */
    public boolean containsKey(String key) {
        return context.containsKey(key);
    }

    /**
     * @return true if there are no mappings defined in this context, false otherwise
     */
    public boolean isEmpty() {
        return context.isEmpty();
    }

    /**
     * @deprecated use {@link #keySet()} instead
     * @return a {@link Set} view of the keys defined in this context
     */
    @Deprecated
    public Set<String> getKeys() {
        return keySet();
    }

    /**
     * @return a {@link Set} view of the keys defined in this context
     */
    public Set<String> keySet() {
        return context.keySet();
    }

    /**
     * Associate value with the given key in this context (overriding if necessary).
     * @param key key
     * @param value value. nullable
     * @return this reference
     */
    public MappingContext put(String key, Object value) {
        context.put(key, value);
        return this;
    }

    /**
     * Copy all of the mappings from the given map to this context (overriding if necessary).
     * @param map map of key-value pairs
     * @return this reference
     */
    public MappingContext putAll(Map<String, Object> map) {
        context.putAll(map);
        return this;
    }

    /**
     * Remove the mapping for a key.
     * @param key key
     * @return this reference
     */
    public MappingContext remove(String key) {
        context.remove(key);
        return this;
    }

    /**
     * Remove all the mappings.
     * @return this reference
     */
    public MappingContext clear() {
        context.clear();
        return this;
    }

    /**
     * @return index in source, -1 unless mapping is taking place over the collection/array
     */
    public int getSourceIndex() {
        return sourceIndex;
    }

    public void setSourceIndex(int sourceIndex) {
        this.sourceIndex = sourceIndex;
    }

    /**
     * @param <T> source item type
     * @return source, null unless mapping is taking place over the collection/array
     */
    @SuppressWarnings("unchecked")
    public <T> Iterable<T> getSource() {
        return source;
    }

    public void setSource(Iterable source) {
        this.source = source;
    }

    protected Map<String, Object> initContext(Map<String, Object> data) {
        if (data == null) {
            return new HashMap<String, Object>();
        }
        return new HashMap<String, Object>(data);
    }

    @Override
    public String toString() {
        return context.toString();
    }
}
