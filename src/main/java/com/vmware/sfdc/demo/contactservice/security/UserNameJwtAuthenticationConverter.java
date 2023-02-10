package com.vmware.sfdc.demo.contactservice.security;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

class UserNameJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserNameJwtAuthenticationConverter.class);

	@Override
	public AbstractAuthenticationToken convert(Jwt jwt) {
		LOGGER.debug("convert({})",jwt);
		JwtGrantedAuthoritiesConverter test = new JwtGrantedAuthoritiesConverter();
		test.setAuthoritiesClaimName("groups");
		test.setAuthorityPrefix("ROLE_");
		Converter<Jwt, Collection<GrantedAuthority>> jwtGrantedAuthoritiesConverter = test;

		Collection<GrantedAuthority> authorities = jwtGrantedAuthoritiesConverter.convert(jwt);

		return new JwtAuthenticationToken(jwt, authorities, getUserName(jwt));
	}

	private String getUserName(Jwt jwt) {
		LOGGER.debug("getUserName({})", jwt);
		if (jwt.hasClaim("name")) {
			LOGGER.info("Username from claim 'name'");
			return jwt.getClaimAsString("name");
		} else if (jwt.hasClaim("user_name")) {
			LOGGER.info("Username from claim 'user_name'");
			return jwt.getClaimAsString("user_name");
		} else {
			LOGGER.info("Username from claim 'subject'");
			return jwt.getSubject();
		}
	}
}
