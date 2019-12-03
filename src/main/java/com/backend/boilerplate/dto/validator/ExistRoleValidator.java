package com.backend.boilerplate.dto.validator;

import com.backend.boilerplate.dao.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Optional;
import java.util.UUID;

/**
 * @author sarvesh
 * @version 0.0.1
 * @since 0.0.1
 */
@Component
public class ExistRoleValidator implements ConstraintValidator<Exist, UUID> {

    @Autowired
    private RoleRepository roleRepository;

    @SuppressWarnings("squid:S3655")
    @Override
    public boolean isValid(UUID roleUuid, ConstraintValidatorContext context) {
        Optional<Long> countOptional = roleRepository.countByUuid(roleUuid);
        return countOptional.get() == 1;
    }
}