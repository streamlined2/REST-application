package com.streamlined.restapp.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.streamlined.restapp.service.notification.Contact;

import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "notification")
public class RecipientsConfigurer {

	@Setter
	private List<Contact> recipients;

	@Bean
	List<Contact> recipients() {
		return recipients;
	}

}
