package com.streamlined.restapp;

import java.lang.reflect.Method;
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

}
