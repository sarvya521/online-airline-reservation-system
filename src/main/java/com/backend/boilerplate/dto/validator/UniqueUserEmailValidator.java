package com.backend.boilerplate.dto.validator;

import com.backend.boilerplate.dao.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Optional;

/**
 * @author sarvesh
 * @version 0.0.1
 * @since 0.0.1
 */
@Component
public class UniqueUserEmailValidator implements ConstraintValidator<UniqueField, String> {

    @Autowired
    private UserRepository userRepository;

    @SuppressWarnings("squid:S3655")
    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        Optional<Long> longOptional = userRepository.countByEmailIgnoreCase(email);
        return longOptional.get() == 0;
    }
}