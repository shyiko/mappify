package com.github.shyiko.mappify.api;

import java.util.Collection;
import java.util.Map;

/**
 * @author <a href="mailto:stanley.shyiko@lifestreetmedia.com">Stanley Shyiko</a>
 */
public abstract class AbstractObjectMapper implements ObjectMapper {

    protected String defaultMappingName = "";

    @Override
    public <T> T map(Object source, T target) {
        return map(source, target, defaultMappingName);
    }

    @Override
    public <T> T map(Object source, Class<T> targetClass) {
        return map(source, targetClass, defaultMappingName);
    }

    @Override
    public <C extends Collection<T>, T> C map(Collection sourceCollection, Class<T> targetClass, C resultCollection) {
        return map(sourceCollection, targetClass, resultCollection, defaultMappingName);
    }

    @Override
    public <S, C extends Map<S, T>, T> C map(Collection<S> sourceCollection, Class<T> targetClass, C resultCollection) {
        return map(sourceCollection, targetClass, resultCollection, defaultMappingName);
    }

    @Override
    public <T> T[] map(Collection sourceCollection, Class<T> targetClass) {
        return map(sourceCollection, targetClass, defaultMappingName);
    }
}
