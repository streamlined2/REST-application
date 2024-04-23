package com.streamlined.restapp;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import com.streamlined.restapp.exception.IntrospectionException;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Utilities {

	public <T> Stream<T> stream(Iterable<T> iterable) {
		return StreamSupport.stream(iterable.spliterator(), false);
	}

	public <T> Object getPropertyValue(Class<T> entityClass, T entity, String propertyName) {
		final String getterName = getGetterName(propertyName);
		try {
			Method getter = entityClass.getMethod(getterName);
			return getter.invoke(entity);
		} catch (ReflectiveOperationException | SecurityException e) {
			throw new IntrospectionException("No accessible getter %s found for entity class %s or execution failed"
					.formatted(getterName, entityClass.getSimpleName()), e);
		}
	}

	private String getGetterName(String propertyName) {
		return "get" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
	}

	public <T> void setPropertyValue(Class<T> entityClass, T entity, String propertyName, Object propertyValue) {
		final String setterName = getSetterName(propertyName);
		try {
			Method setter = entityClass.getMethod(setterName);
			setter.invoke(entity, propertyValue);
		} catch (ReflectiveOperationException | SecurityException e) {
			throw new IntrospectionException("No accessible setter %s found for entity class %s or execution failed"
					.formatted(setterName, entityClass.getSimpleName()), e);
		}
	}

	private String getSetterName(String propertyName) {
		return "set" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
	}

	public <T> T getProbe(Class<T> entityClass, Map<String, Object> parameters) {
		try {
			Constructor<T> personConstructor = entityClass.getConstructor();
			T entity = personConstructor.newInstance();
			for (var parameterEntry : parameters.entrySet()) {
				setPropertyValue(entityClass, entity, parameterEntry.getKey(), parameterEntry.getValue());
			}
			return entity;
		} catch (ReflectiveOperationException | SecurityException e) {
			throw new IntrospectionException("Cannot create instance of entity class %s and set property values"
					.formatted(entityClass.getName()), e);
		}
	}

}
