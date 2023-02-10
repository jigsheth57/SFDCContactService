package com.vmware.sfdc.demo.contactservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
public class ContactserviceApplication {

	private static final Logger LOGGER = LoggerFactory.getLogger(ContactserviceApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(ContactserviceApplication.class, args);
	}

	@Autowired
	@Bean
	public WebClient webClient(@Value("${sfdc.authserviceURL}") String authserviceURL) {
		LOGGER.debug("webClient: authserviceURL {}", authserviceURL);
		return WebClient.builder().baseUrl(authserviceURL).build();
	}

}
