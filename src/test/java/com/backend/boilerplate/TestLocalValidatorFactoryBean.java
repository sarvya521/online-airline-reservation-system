package com.backend.boilerplate;

import org.hibernate.validator.HibernateValidator;
import org.reflections.Reflections;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.mock.web.MockServletContext;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.context.support.GenericWebApplicationContext;

import javax.validation.ConstraintValidator;
import java.util.Map;
import java.util.Set;

/**
 * @author sarvesh
 * @version 0.0.1
 * @since 0.0.1
 */
public final class TestLocalValidatorFactoryBean extends LocalValidatorFactoryBean {

    private static final String VALIDATORS_PACKAGE = "com.backend.boilerplate.dto.validator";

    public TestLocalValidatorFactoryBean(MockServletContext servletContext, Map<String, JpaRepository> repositories) throws Exception {
        super();
        final GenericWebApplicationContext context = new GenericWebApplicationContext(servletContext);
        final ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();
        Set<Class<? extends ConstraintValidator>> constraintValidators = loadOwConstraintValidators();
        constraintValidators.forEach(clazz -> {
            try {
                beanFactory.registerSingleton(clazz.getCanonicalName(), clazz.getDeclaredConstructor().newInstance());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        context.refresh();
        this.setApplicationContext(context);
        TestConstraintValidationFactory constraintFactory =
            new TestConstraintValidationFactory(context, constraintValidators, repositories);
        this.setConstraintValidatorFactory(constraintFactory);
        this.setProviderClass(HibernateValidator.class);
        this.afterPropertiesSet();
    }

    private Set<Class<? extends ConstraintValidator>> loadOwConstraintValidators() {
        Reflections reflections = new Reflections(VALIDATORS_PACKAGE);
        return reflections.getSubTypesOf(ConstraintValidator.class);
    }
}
