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
package com.github.shyiko.mappify.handcraft;

import com.github.shyiko.mappify.api.MappingException;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * @author <a href="mailto:stanley.shyiko@gmail.com">Stanley Shyiko</a>
 */
public class HandcraftMapperTest {

    @Test
    public void testMap() throws Exception {
        class MappingProvider {
            @Mapping
            public void mapFromSourceToTarget(Source source, Target target) {
                target.name = "Target #" + source.id;
            }
        }
        HandcraftMapper handcraftMapper = new HandcraftMapper();
        handcraftMapper.register(new MappingProvider());
        Target target = handcraftMapper.map(new Source(7), Target.class);
        assertEquals(target.name, "Target #7");
    }

    @Test(expectedExceptions = MappingException.class)
    public void testMapWithoutOverlayDefinitionOntoInstance() throws Exception {
        class MappingProvider {
            @Mapping
            public Target mapFromSourceToTarget(Source source) {
                Target target = new Target();
                target.name = "Target #" + source.id;
                return target;
            }
        }
        HandcraftMapper handcraftMapper = new HandcraftMapper();
        handcraftMapper.register(new MappingProvider());
        handcraftMapper.map(new Source(7), new Target());
    }

    @Test
    public void testMapWithImmutableClass() throws Exception {
        class MappingProvider {
            @Mapping
            public ImmutableTarget mapFromSourceToTarget(Source source) {
                ImmutableTarget.Builder targetBuilder = new ImmutableTarget.Builder();
                targetBuilder.setName("Target #" + source.id);
                return targetBuilder.build();
            }
        }
        HandcraftMapper handcraftMapper = new HandcraftMapper();
        handcraftMapper.register(new MappingProvider());
        ImmutableTarget target = handcraftMapper.map(new Source(7), ImmutableTarget.class);
        assertEquals(target.getName(), "Target #7");
    }

    public static class Source {

        private int id;

        public Source(int id) {
            this.id = id;
        }
    }

    public static class Target {

        private String name;
    }

    public static class ImmutableTarget {

        private String name;

        private ImmutableTarget() {
        }

        public String getName() {
            return name;
        }

        public static class Builder {

            private ImmutableTarget target = new ImmutableTarget();

            public void setName(String name) {
                target.name = name;
            }

            public ImmutableTarget build() {
                return target;
            }
        }
    }
}
