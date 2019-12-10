package com.oars.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

@Configuration
public class ErrorMessageSourceConfig {

    @Bean
    public MessageSource errorMessageSource() {
        ReloadableResourceBundleMessageSource messageSource
            = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:errors-list");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }
}
