package com.streamlined.restapp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.streamlined.restapp.service.notification.Contact;

import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "notification")
public class SenderConfigurer {
	
	@Setter
	private Contact sender;
	
	@Bean
	Contact sender() {
		return sender;
	}

}
