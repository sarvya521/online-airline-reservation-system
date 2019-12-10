package com.oars.dto.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintValidatorFactory;

/**
 * @author sarvesh
 * @version 0.0.1
 * @since 0.0.1
 */
@Component
public class UniqueResourceValidator implements ConstraintValidator<UniqueResource, Object> {

    @Autowired
    private ConstraintValidatorFactory constraintValidatorFactory;

    private ConstraintValidator constraintValidator;

    @SuppressWarnings("unchecked")
    @Override
    public void initialize(UniqueResource constraintAnnotation) {
        constraintValidator = constraintValidatorFactory.getInstance(constraintAnnotation.constraintValidator());
        constraintValidator.initialize(constraintAnnotation);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        return constraintValidator.isValid(value, context);
    }
}