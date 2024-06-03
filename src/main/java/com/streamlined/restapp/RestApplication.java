package com.streamlined.restapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.ConversionService;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.streamlined.restapp.config.RecipientsConfigurer;
import com.streamlined.restapp.config.SenderConfigurer;

/**
 * Main class of the application
 */

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties({ RecipientsConfigurer.class, SenderConfigurer.class })
public class RestApplication {

	public static void main(String[] args) {
		SpringApplication.run(RestApplication.class, args);
	}

	@Bean
	ConversionService conversionService() {
		return new DefaultFormattingConversionService();
	}

}
