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
package com.github.shyiko.mappify.handcraft.spring;

import com.github.shyiko.mappify.handcraft.HandcraftMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.util.Collection;

/**
 * {@link BeanPostProcessor} which discovers mappings by scanning Spring context for {@link MappingProvider}
 * annotated beans.
 *
 * @author <a href="mailto:stanley.shyiko@gmail.com">Stanley Shyiko</a>
 */
public class HandcraftMapperInitializingBean implements BeanPostProcessor {

    private static final Logger logger = LoggerFactory.getLogger(HandcraftMapperInitializingBean.class);

    private HandcraftMapper mapper;

    public void setMapper(HandcraftMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean.getClass().isAnnotationPresent(MappingProvider.class)) {
            Collection<HandcraftMapper.MappingIdentifier> mappings = mapper.loadMappingsFrom(bean);
            if (logger.isDebugEnabled()) {
                for (HandcraftMapper.MappingIdentifier mapping : mappings) {
                    logger.debug("Discovered mapping " + mapping);
                }
            }
        }
        return bean;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
}
