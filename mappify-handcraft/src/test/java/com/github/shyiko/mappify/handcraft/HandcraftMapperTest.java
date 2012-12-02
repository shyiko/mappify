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
        handcraftMapper.loadMappingsFrom(new MappingProvider());
        Target target = handcraftMapper.map(new Source(7), Target.class);
        assertEquals(target.name, "Target #7");
    }

    public static class Source { int id; Source(int id) { this.id = id; } }
    public static class Target { String name; }
}
