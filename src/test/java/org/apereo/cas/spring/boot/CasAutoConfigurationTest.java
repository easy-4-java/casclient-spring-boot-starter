package org.apereo.cas.spring.boot;

import jakarta.servlet.Filter;
import jakarta.servlet.http.HttpSessionListener;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = CasAutoConfigurationTest.TestApplication.class,
        properties = {"cas.enabled=true", "cas.error.error-redirect-url=/error"})
class CasAutoConfigurationTest {

    @SpringBootConfiguration
    @EnableAutoConfiguration
    static class TestApplication {
    }

    @Autowired
    private ApplicationContext context;

    @Test
    void registersJakartaCasFilterAndListener() {
        FilterRegistrationBean<?> filter = context.getBean("singleSignOutFilter", FilterRegistrationBean.class);
        ServletListenerRegistrationBean<?> listener = context.getBean(
                "singleSignOutHttpSessionListener", ServletListenerRegistrationBean.class);

        assertThat(filter.getFilter()).isInstanceOf(Filter.class);
        assertThat(listener.getListener()).isInstanceOf(HttpSessionListener.class);
    }
}
