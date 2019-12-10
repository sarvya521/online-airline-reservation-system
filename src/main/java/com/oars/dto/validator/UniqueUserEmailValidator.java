package com.oars.dto.validator;

import com.oars.dao.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Optional;


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