package org.apereo.cas.spring.boot;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = CasDisabledAutoConfigurationTest.TestApplication.class)
class CasDisabledAutoConfigurationTest {

    @SpringBootConfiguration
    @EnableAutoConfiguration
    static class TestApplication {
    }

    @Autowired
    private ApplicationContext context;

    @Test
    void doesNotRegisterCasFiltersByDefault() {
        assertThat(context.getBeansOfType(FilterRegistrationBean.class)).doesNotContainKey("singleSignOutFilter");
    }
}
