/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.wicket.spring.test;

import java.io.IOException;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanNotOfRequiredTypeException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.core.ResolvableType;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;

/**
 * Mock application context object. This mock context allows easy creation of unit tests by allowing
 * the user to put bean instances into the context.
 * 
 * Only {@link #getBean(String)}, {@link #getBean(String, Class)}, and
 * {@link #getBeansOfType(Class)
 * } are implemented so far. Any other method throws
 * {@link UnsupportedOperationException}.
 * 
 * @author Igor Vaynberg (ivaynberg)
 * 
 */
public class ApplicationContextMock implements ApplicationContext, Serializable
{
	private static final long serialVersionUID = 1L;

	private final Map<String, Object> beans = new HashMap<>();

	/**
	 * puts bean with the given name into the context
	 * 
	 * @param name
	 * @param bean
	 */
	public void putBean(final String name, final Object bean)
	{
		if (beans.containsKey(name))
		{
			throw new IllegalArgumentException(new StringBuilder().append("a bean with name [").append(name).append("] has already been added to the context").toString());
		}
		beans.put(name, bean);
	}

	/**
	 * puts bean with into the context. bean object's class name will be used as the bean name.
	 * 
	 * @param bean
	 */
	public void putBean(final Object bean)
	{
		putBean(bean.getClass().getName(), bean);
	}

	@Override
	public Object getBean(final String name)
	{
		Object bean = beans.get(name);
		if (bean == null)
		{
			throw new NoSuchBeanDefinitionException(name);
		}
		return bean;
	}

	@Override
	public Object getBean(final String name, final Object... args)
	{
		return getBean(name);
	}

	/**
	 * @see org.springframework.beans.factory.BeanFactory#getBean(java.lang.String, java.lang.Class)
	 */
	@Override
	@SuppressWarnings({ "unchecked" })
	public <T> T getBean(String name, Class<T> requiredType)
	{
		Object bean = getBean(name);
		if (!(requiredType.isAssignableFrom(bean.getClass())))
		{
			throw new BeanNotOfRequiredTypeException(name, requiredType, bean.getClass());
		}
		return (T)bean;
	}

	/**
	 * @see org.springframework.beans.factory.ListableBeanFactory#getBeansOfType(java.lang.Class)
	 */
	@Override
	@SuppressWarnings({ "unchecked" })
	public <T> Map<String, T> getBeansOfType(Class<T> type)
	{
		final Map<String, T> found = new HashMap<>();

		beans.entrySet().stream().filter(entry -> type.isAssignableFrom(entry.getValue().getClass())).forEach(entry -> found.put(entry.getKey(), (T) entry.getValue()));

		return found;
	}

	@Override
	public <T> T getBean(Class<T> requiredType)
	{
		Iterator<T> beans = getBeansOfType(requiredType).values().iterator();

		if (beans.hasNext() == false)
		{
			throw new NoSuchBeanDefinitionException(new StringBuilder().append("bean of required type ").append(requiredType).append(" not found").toString());
		}
		final T bean = beans.next();

		if (beans.hasNext() != false)
		{
			throw new NoSuchBeanDefinitionException(new StringBuilder().append("more than one bean of required type ").append(requiredType).append(" found").toString());
		}
		return bean;
	}

	@Override
	public <T> T getBean(Class<T> requiredType, Object... objects)
	{
		return getBean(requiredType);
	}

	@Override
	public <T> ObjectProvider<T> getBeanProvider(Class<T> aClass)
	{
		return null;
	}

	@Override
	public <T> ObjectProvider<T> getBeanProvider(ResolvableType resolvableType)
	{
		return null;
	}

	@Override
	public Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> annotationType)
	{
		final Map<String, Object> found = new HashMap<>();

		beans.entrySet().stream().filter(entry -> entry.getValue().getClass().isAnnotationPresent(annotationType)).forEach(entry -> found.put(entry.getKey(), entry.getValue()));
		return found;
	}

	@Override
	public <A extends Annotation> A findAnnotationOnBean(String beanName, Class<A> annotationType)
	{
		return findAnnotationOnClass(getBean(beanName).getClass(), annotationType);
	}

	private <A extends Annotation> A findAnnotationOnClass(Class<?> cls, Class<A> annotationType)
	{
		// lookup annotation type on class
		A annotation = cls.getAnnotation(annotationType);

		// lookup annotation type on superclass
		if (annotation == null && cls.getSuperclass() != null)
		{
			annotation = findAnnotationOnClass(cls.getSuperclass(), annotationType);
		}

		// lookup annotation type on interfaces
		if (annotation == null)
		{
			for (Class<?> intfClass : cls.getInterfaces())
			{
				annotation = findAnnotationOnClass(intfClass, annotationType);

				if (annotation != null)
				{
					break;
				}
			}
		}

		return annotation;
	}

	@Override
	public ApplicationContext getParent()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public String getDisplayName()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public long getStartupDate()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void publishEvent(final ApplicationEvent event)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void publishEvent(Object o)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsBeanDefinition(final String beanName)
	{
		return containsBean(beanName);
	}

	@Override
	public int getBeanDefinitionCount()
	{
		return beans.size();
	}

	@Override
	public String[] getBeanDefinitionNames()
	{
		return beans.keySet().toArray(new String[0]);
	}

	@Override
	public String[] getBeanNamesForType(ResolvableType resolvableType)
	{
		return new String[0];
	}

	@Override
	public String[] getBeanNamesForType(ResolvableType resolvableType, boolean includeNonSingletons, boolean allowEagerInit)
	{
		return new String[0];
	}

	@Override
	@SuppressWarnings({ "unchecked" })
	public String[] getBeanNamesForType(final Class type)
	{
		ArrayList<String> names = new ArrayList<>();
		beans.entrySet().forEach(entry -> {
			Object bean = entry.getValue();

			if (type.isAssignableFrom(bean.getClass()))
			{
				names.add(entry.getKey());
			}
		});
		return names.toArray(new String[names.size()]);
	}

	@Override
	@SuppressWarnings({ "unchecked" })
	public String[] getBeanNamesForType(Class type, boolean includeNonSingletons,
		boolean allowEagerInit)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> Map<String, T> getBeansOfType(Class<T> type, boolean includeNonSingletons,
		boolean allowEagerInit)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public String[] getBeanNamesForAnnotation(Class<? extends Annotation> aClass)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsBean(final String name)
	{
		return beans.containsKey(name);
	}

	@Override
	public boolean isSingleton(final String name)
	{
		return true;
	}

	@Override
	public Class<?> getType(final String name)
	{
		return getType(name, true);
	}

	@Override
	public Class<?> getType(String name, boolean allowFactoryBeanInit)
	{
		Object bean = beans.get(name);
		if (bean == null)
		{
			throw new NoSuchBeanDefinitionException(new StringBuilder().append("No bean with name '").append(name).append("'").toString());
		}

		if (bean instanceof FactoryBean)
		{
			return ((FactoryBean) bean).getObjectType();
		}
		
		return bean.getClass();
	}

	@Override
	public String[] getAliases(final String name)
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * @see org.springframework.beans.factory.HierarchicalBeanFactory# getParentBeanFactory()
	 */
	@Override
	public BeanFactory getParentBeanFactory()
	{
		return null;
	}

	@Override
	public String getMessage(final String code, final Object[] args, final String defaultMessage,
		final Locale locale)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public String getMessage(final String code, final Object[] args, final Locale locale)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public String getMessage(final MessageSourceResolvable resolvable, final Locale locale)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Resource[] getResources(final String locationPattern) throws IOException
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Resource getResource(final String location)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public AutowireCapableBeanFactory getAutowireCapableBeanFactory()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsLocalBean(final String arg0)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public ClassLoader getClassLoader()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public String getId()
	{
		return null;
	}

	@Override
	public String getApplicationName()
	{
		return "";
	}

	@Override
	public boolean isPrototype(final String name)
	{
		return !isSingleton(name);
	}

	@Override
	public boolean isTypeMatch(String s, ResolvableType resolvableType)
	{
		return false;
	}

	@Override
	@SuppressWarnings({ "unchecked" })
	public boolean isTypeMatch(final String name, final Class targetType)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Environment getEnvironment()
	{
		return null;
	}
}
