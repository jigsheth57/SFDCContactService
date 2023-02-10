package com.vmware.sfdc.demo.contactservice.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;

@EnableWebFluxSecurity
@Configuration
@Profile("k8s")
public class KubernetesSecurityConfiguration {

	private static final Logger LOGGER = LoggerFactory.getLogger(KubernetesSecurityConfiguration.class);

	@Bean
	public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity httpSecurity) {
		LOGGER.debug("securityWebFilterChain(): k8s");
		httpSecurity
				.authorizeExchange(exchanges -> exchanges.anyExchange().authenticated())
				.oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(
						new ReactiveJwtAuthenticationConverterAdapter(new UserNameJwtAuthenticationConverter()))));

		return httpSecurity.build();
	}

}
