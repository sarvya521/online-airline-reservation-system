package com.oars.util;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;


@Component
public class ErrorGeneratorInitializer {

    @Getter
    @Autowired
    private MessageSource errorMessageSource;

    @PostConstruct
    public void setErrorMessageSource() {
        ErrorGenerator.setErrorMessageSource(errorMessageSource);
    }
}
