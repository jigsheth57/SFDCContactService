package com.vmware.sfdc.demo.contactservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.web.reactive.function.client.WebClient;

import com.vmware.sfdc.demo.contactservice.model.Contact;

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

	@Bean("contactTemplate")
	public ReactiveRedisTemplate<String, Contact> contactTemplate(
			ReactiveRedisConnectionFactory factory) {
		StringRedisSerializer keySerializer = new StringRedisSerializer();
		Jackson2JsonRedisSerializer<Contact> valueSerializer = new Jackson2JsonRedisSerializer<>(Contact.class);
		RedisSerializationContext.RedisSerializationContextBuilder<String, Contact> builder = RedisSerializationContext
				.newSerializationContext(keySerializer);
		RedisSerializationContext<String, Contact> context = builder.value(valueSerializer).build();
		return new ReactiveRedisTemplate<>(factory, context);
	}

}
