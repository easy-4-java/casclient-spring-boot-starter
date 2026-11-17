package org.apereo.cas.spring.boot;

import org.apereo.cas.client.authentication.Saml11AuthenticationFilter;
import org.apereo.cas.client.validation.Cas10TicketValidationFilter;
import org.apereo.cas.client.validation.Cas20ProxyReceivingTicketValidationFilter;
import org.apereo.cas.client.validation.Cas30ProxyReceivingTicketValidationFilter;
import org.apereo.cas.client.validation.Saml11TicketValidationFilter;
import org.apereo.cas.spring.boot.CasProperties.CasProtocol;
import org.apereo.cas.spring.boot.property.CasAuthcProperties;
import org.apereo.cas.spring.boot.property.CasErrorProperties;
import org.apereo.cas.spring.boot.property.CasTicketProperties;
import org.apereo.cas.spring.boot.property.CasWrapperProperties;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.servlet.FilterRegistrationBean;

import static org.assertj.core.api.Assertions.assertThat;

class CasFilterConfigurationTest {

    private final CasFilterAutoConfiguration configuration = new CasFilterAutoConfiguration();

    @Test
    void defaultCas20HasTicketValidator() {
        FilterRegistrationBean<?> registration = configuration.ticketValidationFilter(
                new CasProperties(), new CasTicketProperties());
        assertThat(registration.getFilter()).isInstanceOf(Cas20ProxyReceivingTicketValidationFilter.class);
    }

    @Test
    void selectsEachSupportedTicketProtocol() {
        CasProperties cas = new CasProperties();
        CasTicketProperties ticket = new CasTicketProperties();
        cas.setProtocol(CasProtocol.CAS10);
        assertThat(configuration.ticketValidationFilter(cas, ticket).getFilter())
                .isInstanceOf(Cas10TicketValidationFilter.class);
        cas.setProtocol(CasProtocol.CAS30);
        assertThat(configuration.ticketValidationFilter(cas, ticket).getFilter())
                .isInstanceOf(Cas30ProxyReceivingTicketValidationFilter.class);
        cas.setProtocol(CasProtocol.CAS30_PROXY);
        assertThat(configuration.ticketValidationFilter(cas, ticket).getFilter())
                .isInstanceOf(Cas30ProxyReceivingTicketValidationFilter.class);
        cas.setProtocol(CasProtocol.SAML);
        assertThat(configuration.ticketValidationFilter(cas, ticket).getFilter())
                .isInstanceOf(Saml11TicketValidationFilter.class);
    }

    @Test
    void transfersProxyTicketOptions() {
        CasProperties cas = new CasProperties();
        cas.setProtocol(CasProtocol.CAS20_PROXY);
        cas.setAllowedProxyChains("https://proxy.example");
        cas.setArtifactParameterName("customTicket");
        cas.setLogoutParameterName("logout");
        cas.setRelayStateParameterName("relay");
        cas.setServerName("https://app.example");
        CasTicketProperties ticket = new CasTicketProperties();
        ticket.setAuthenticationRedirectStrategyClass("RedirectStrategy");
        ticket.setCipherAlgorithm("AES");
        ticket.setGatewayStorageClass("GatewayStorage");
        ticket.setIgnorePattern("/public/**");
        ticket.setProxyReceptorUrl("/proxy");
        ticket.setProxyCallbackUrl("https://app.example/callback");
        ticket.setProxyGrantingTicketStorageClass("TicketStorage");
        ticket.setRoleAttribute("roles");
        ticket.setSecretKey("secret");
        ticket.setTicketValidatorClass("TicketValidator");
        ticket.setHostnameVerifier("Verifier");
        ticket.setHostnameVerifierConfig("config");
        ticket.setSslConfigFile("ssl.properties");
        FilterRegistrationBean<?> registration = configuration.ticketValidationFilter(cas, ticket);
        assertThat(registration.getFilter()).isInstanceOf(Cas20ProxyReceivingTicketValidationFilter.class);
        assertThat(registration.getInitParameters()).containsEntry("allowedProxyChains", "https://proxy.example")
                .containsEntry("roleAttribute", "roles").containsEntry("hostnameVerifier", "Verifier");
    }

    @Test
    void transfersOptionalFilterOptions() {
        CasProperties cas = new CasProperties();
        cas.setEnabled(true);
        cas.setLogoutParameterName("logout");
        cas.setRelayStateParameterName("relay");
        CasErrorProperties error = new CasErrorProperties();
        error.setErrorRedirectUrl("/error");
        error.getErrorRedirectMappings().put("java.lang.Exception", "/error");
        assertThat(configuration.errorRedirectFilter(error).getInitParameters())
                .containsEntry("java.lang.Exception", "/error");
        assertThat(configuration.singleSignOutFilter(cas, new org.apereo.cas.spring.boot.property.CasSsoProperties())
                .getInitParameters()).containsKey("logoutParameterName");
        CasWrapperProperties wrapper = new CasWrapperProperties();
        wrapper.setRoleAttribute("roles");
        assertThat(configuration.requestWrapperFilter(wrapper).getInitParameters())
                .containsEntry("roleAttribute", "roles");
        cas.setProtocol(CasProtocol.SAML);
        assertThat(configuration.authenticationFilter(cas, new CasAuthcProperties(),
                new org.apereo.cas.client.authentication.DefaultGatewayResolverImpl(),
                new AntUrlPatternMatcherStrategy()).getFilter())
                .isInstanceOf(Saml11AuthenticationFilter.class);
    }

    @Test
    void antMatcherHandlesMatchingAndNonMatchingPaths() {
        AntUrlPatternMatcherStrategy matcher = new AntUrlPatternMatcherStrategy();
        matcher.setPattern("/public/**, /health");
        assertThat(matcher.matches("/public/index")).isTrue();
        assertThat(matcher.matches("/private")).isFalse();
    }
}
