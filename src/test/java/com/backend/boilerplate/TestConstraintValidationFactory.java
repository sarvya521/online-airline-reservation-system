package com.backend.boilerplate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.bind.support.SpringWebConstraintValidatorFactory;
import org.springframework.web.context.WebApplicationContext;

import javax.validation.ConstraintValidator;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

/**
 * @author sarvesh
 * @version 0.0.1
 * @since 0.0.1
 */
public class TestConstraintValidationFactory extends SpringWebConstraintValidatorFactory {

    private final WebApplicationContext ctx;

    private final Set<Class<? extends ConstraintValidator>> constraintValidators;

    private final Map<String, JpaRepository> repositories;

    public TestConstraintValidationFactory(WebApplicationContext ctx,
                                           Set<Class<? extends ConstraintValidator>> constraintValidators,
                                           Map<String, JpaRepository> repositories) {
        this.ctx = ctx;
        this.constraintValidators = constraintValidators;
        this.repositories = repositories;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends ConstraintValidator<?, ?>> T getInstance(Class<T> key) {
        ConstraintValidator instance = super.getInstance(key);
        constraintValidators.stream()
            .filter(clazz -> clazz.isInstance(instance))
            .findFirst()
            .ifPresent(clazz -> {
                Arrays.stream(clazz.getDeclaredFields())
                    .filter(field -> "constraintValidatorFactory".equals(field.getName()))
                    .forEach(field -> {
                        ReflectionTestUtils.setField(instance, "constraintValidatorFactory", this);
                    });
                Arrays.stream(clazz.getDeclaredFields())
                    .filter(field -> repositories.containsKey(field.getName()))
                    .map(Field::getName)
                    .forEach(fieldName -> {
                        ReflectionTestUtils.setField(instance, fieldName, repositories.get(fieldName));
                    });
            });
        return (T) instance;
    }

    @Override
    protected WebApplicationContext getWebApplicationContext() {
        return ctx;
    }
}