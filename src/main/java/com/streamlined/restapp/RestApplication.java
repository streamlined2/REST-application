package com.streamlined.restapp;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class RestApplication implements WebMvcConfigurer {

	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		WebMvcConfigurer.super.configureMessageConverters(converters);
		var converter = new FormHttpMessageConverter();
		converter.addSupportedMediaTypes(new MediaType("application", "x-www-form-urlencoded", StandardCharsets.UTF_8));
		converters.add(converter);
	}

	public static void main(String[] args) {
		SpringApplication.run(RestApplication.class, args);
	}

}
