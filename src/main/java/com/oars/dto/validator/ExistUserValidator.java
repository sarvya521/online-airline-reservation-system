package com.oars.dto.validator;

import com.oars.dao.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Optional;
import java.util.UUID;


@Component
public class ExistUserValidator implements ConstraintValidator<Exist, UUID> {

    @Autowired
    private UserRepository userRepository;

    @SuppressWarnings("squid:S3655")
    @Override
    public boolean isValid(UUID userUuid, ConstraintValidatorContext context) {
        Optional<Long> countOptional = userRepository.countByUuid(userUuid);
        return countOptional.get() == 1;
    }
}