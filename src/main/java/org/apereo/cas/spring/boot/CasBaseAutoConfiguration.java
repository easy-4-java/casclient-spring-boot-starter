package org.apereo.cas.spring.boot;

import org.apereo.cas.client.authentication.AuthenticationFilter;
import org.apereo.cas.client.authentication.AuthenticationRedirectStrategy;
import org.apereo.cas.client.authentication.DefaultAuthenticationRedirectStrategy;
import org.apereo.cas.client.authentication.DefaultGatewayResolverImpl;
import org.apereo.cas.client.authentication.GatewayResolver;
import org.apereo.cas.client.authentication.UrlPatternMatcherStrategy;
import org.apereo.cas.client.proxy.ProxyGrantingTicketStorage;
import org.apereo.cas.client.proxy.ProxyGrantingTicketStorageImpl;
import org.apereo.cas.client.session.HashMapBackedSessionMappingStorage;
import org.apereo.cas.client.session.SessionMappingStorage;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.autoconfigure.AutoConfiguration;

@AutoConfiguration
@ConditionalOnClass(AuthenticationFilter.class)
@ConditionalOnProperty(prefix = CasProperties.PREFIX, value = "enabled", havingValue = "true")
@EnableConfigurationProperties({ CasProperties.class })
public class CasBaseAutoConfiguration {
	 	
	@Bean
	@ConditionalOnMissingBean
	public ProxyGrantingTicketStorage proxyGrantingTicketStorage(CasProperties casProperties) {
		return new ProxyGrantingTicketStorageImpl(casProperties.getTimeout());
	}
 
	@Bean
	@ConditionalOnMissingBean
	public GatewayResolver gatewayStorage() {
		GatewayResolver gatewayStorage = new DefaultGatewayResolverImpl();
		return gatewayStorage;
	}

	@Bean
	@ConditionalOnMissingBean
	public AuthenticationRedirectStrategy authenticationRedirectStrategy() {
		AuthenticationRedirectStrategy authenticationRedirectStrategy = new DefaultAuthenticationRedirectStrategy();
		return authenticationRedirectStrategy;
	}
	
	@Bean
	@ConditionalOnMissingBean
	public UrlPatternMatcherStrategy ignoreUrlPatternMatcherStrategy() {
		UrlPatternMatcherStrategy ignoreUrlPatternMatcherStrategy = new AntUrlPatternMatcherStrategy();
		return ignoreUrlPatternMatcherStrategy;
	}
	
	@Bean
	@ConditionalOnMissingBean
	public SessionMappingStorage sessionMappingStorage() {
		return new HashMapBackedSessionMappingStorage();
	}
}
