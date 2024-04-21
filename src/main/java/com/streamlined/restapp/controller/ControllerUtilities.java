package com.streamlined.restapp.controller;

import java.net.URI;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.http.HttpServletRequest;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ControllerUtilities {

	private static final String PAGE_PARAMETER = "page";
	private static final int DEFAULT_PAGE_NUMBER = 1;
	private static final String SIZE_PARAMETER = "size";
	private static final int DEFAULT_SIZE_VALUE = 5;

	public URI getResourceURI(HttpServletRequest servletRequest, Long id) {
		return UriComponentsBuilder.fromHttpUrl(servletRequest.getRequestURL().toString()).pathSegment("{id}")
				.build(id);
	}

	public int getPageNumber(Map<String, Object> parameters) {
		var pageNumber = parameters.get(PAGE_PARAMETER);
		if (pageNumber == null) {
			return DEFAULT_PAGE_NUMBER;
		}
		try {
			if (pageNumber instanceof Integer i) {
				return i.intValue();
			} else if (pageNumber instanceof String s) {
				return Integer.parseInt(s);
			}
			return DEFAULT_PAGE_NUMBER;
		} catch (NumberFormatException e) {
			return DEFAULT_PAGE_NUMBER;
		}
	}

	public int getPageSize(Map<String, Object> parameters) {
		var pageSize = parameters.get(SIZE_PARAMETER);
		if (pageSize == null) {
			return DEFAULT_SIZE_VALUE;
		}
		try {
			if (pageSize instanceof Integer i) {
				return i.intValue();
			} else if (pageSize instanceof String s) {
				return Integer.parseInt(s);
			}
			return DEFAULT_SIZE_VALUE;
		} catch (NumberFormatException e) {
			return DEFAULT_SIZE_VALUE;
		}
	}

	public Map<String, Object> getFilterParameters(Map<String, Object> parameters) {
		return parameters.entrySet().stream().filter(ControllerUtilities::isNotReservedParameter)
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}

	private boolean isNotReservedParameter(Map.Entry<String, Object> entry) {
		return isNotReservedParameter(entry.getKey(), PAGE_PARAMETER, SIZE_PARAMETER);
	}

	private boolean isNotReservedParameter(Object obj, Object... values) {
		return !Arrays.asList(values).contains(obj);
	}

}
