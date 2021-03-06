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

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * Each class annotated with this annotation will be examined for {@link com.github.shyiko.mappify.handcraft.Mapping}
 * methods by {@link HandcraftMapperInitializingBean}.
 *
 * @author <a href="mailto:stanley.shyiko@gmail.com">Stanley Shyiko</a>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Component
public @interface MappingProvider {
}
