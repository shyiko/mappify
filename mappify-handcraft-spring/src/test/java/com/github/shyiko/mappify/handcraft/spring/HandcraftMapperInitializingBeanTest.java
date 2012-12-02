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

import com.github.shyiko.mappify.api.Mapper;
import com.github.shyiko.mappify.handcraft.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

import static org.testng.Assert.assertEquals;

/**
 * @author <a href="mailto:stanley.shyiko@gmail.com">Stanley Shyiko</a>
 */
@MappingProvider
@ContextConfiguration(locations = {"classpath:mappify-handcraft-spring-context-test.xml"})
public class HandcraftMapperInitializingBeanTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private Mapper mapper;

    @Test
    public void testMapping() {
        Entity entity = new Entity(1, Arrays.asList(new EntityProperty("p1"), new EntityProperty("p2")));
        EntityDTO dto = mapper.map(entity, EntityDTO.class);
        assertEquals(dto.id, 1);
        assertEquals(dto.properties.size(), 2);
    }

    @Mapping
    public void mapToDTO(Entity entity, EntityDTO entityDTO) {
        entityDTO.id = entity.id;
        entityDTO.properties = mapper.map(entity.properties, EntityPropertyDTO.class,
                new LinkedList<EntityPropertyDTO>());
    }

    @Mapping
    public void mapToDTO(EntityProperty entity, EntityPropertyDTO entityDTO) {
        entityDTO.name = entity.name.toUpperCase();
    }

    public static class Entity {
        private int id;
        private Collection<EntityProperty> properties;
        public Entity(int id, Collection<EntityProperty> properties) {
            this.id = id;
            this.properties = properties;
        }
    }

    public static class EntityProperty {
        private String name;
        public EntityProperty(String name) {
            this.name = name;
        }
    }

    public static class EntityDTO {
        private int id;
        private Collection<EntityPropertyDTO> properties;
    }

    public static class EntityPropertyDTO {
        private String name;
    }
}
