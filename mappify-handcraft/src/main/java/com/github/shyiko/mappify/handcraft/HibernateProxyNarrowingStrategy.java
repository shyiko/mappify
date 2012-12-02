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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * {@link ProxyNarrowingStrategy} which relies on HibernateProxyHelper.getClassWithoutInitializingProxy(...)
 * to perform class narrowing. If Hibernate isn't available on the classpath, it returns object class by
 * calling {@link Object#getClass()} on the passed instance.
 *
 * @author <a href="mailto:stanley.shyiko@gmail.com">Stanley Shyiko</a>
 */
public class HibernateProxyNarrowingStrategy implements ProxyNarrowingStrategy {

    private static final Logger logger = LoggerFactory.getLogger(HibernateProxyNarrowingStrategy.class);

    private Method hibernateProxyHelperNarrowMethod;

    public HibernateProxyNarrowingStrategy() {
        String hibernateProxyHelperClass = "org.hibernate.proxy.HibernateProxyHelper",
                hibernateProxyHelperNarrowMethodName = "getClassWithoutInitializingProxy";
        try {
            Class<?> hibernateProxyHelper = Class.forName(hibernateProxyHelperClass);
            hibernateProxyHelperNarrowMethod = hibernateProxyHelper.getMethod(hibernateProxyHelperNarrowMethodName,
                    Object.class);
        } catch (ClassNotFoundException e) {
            if (logger.isDebugEnabled()) {
                logger.debug(hibernateProxyHelperClass + " wasn't loaded. Hibernate proxy narrowing disabled.");
            }
        } catch (NoSuchMethodException e) {
            if (logger.isDebugEnabled()) {
                logger.debug(hibernateProxyHelperClass + " has no method named " + hibernateProxyHelperNarrowMethodName +
                        ". Hibernate proxy narrowing disabled.");
            }
        }
    }

    @Override
    public Class narrow(Object target) {
        if (hibernateProxyHelperNarrowMethod != null) {
            try {
                return (Class) hibernateProxyHelperNarrowMethod.invoke(null, target);
            } catch (IllegalAccessException e) {
                throw new MappingException("Failed to narrow Hibernate proxy", e);
            } catch (InvocationTargetException e) {
                throw new MappingException("Failed to narrow Hibernate proxy", e);
            }
        }
        return target.getClass();
    }
}
