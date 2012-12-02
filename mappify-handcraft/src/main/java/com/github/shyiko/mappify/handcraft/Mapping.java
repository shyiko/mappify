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

import java.lang.annotation.*;

/**
 * Marks method to be used for the object mapping.<p/>
 * Method signature must conform either
 * <pre>
 * public void methodName(TypeOfTheSourceObject sourceObject, TypeOfTheTargetObject targetObject) { ... }
 * </pre>
 * or
 * <pre>
 * public void methodName(TypeOfTheSourceObject sourceObject, TypeOfTheTargetObject targetObject,
 * MappingContext context) { ... }
 * </pre>
 * or
 * <pre>
 * public TypeOfTheTargetObject methodName(TypeOfTheSourceObject sourceObject) { ... }
 * // for obvious reasons, this type of mapping requires TypeOfTheTargetObject.class to be specified
 * // as a second parameter for Mapper.map(...) calls
 * </pre>
 * or
 * <pre>
 * public TypeOfTheTargetObject methodName(TypeOfTheSourceObject sourceObject, MappingContext context) { ... }
 * // for obvious reasons, this type of mapping requires TypeOfTheTargetObject.class to be specified
 * // as a second parameter for Mapper.map(...) calls
 * </pre>
 * @author <a href="mailto:stanley.shyiko@gmail.com">Stanley Shyiko</a>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Mapping {

    /**
     * @return name of the mapping
     */
    String value() default "";
}
