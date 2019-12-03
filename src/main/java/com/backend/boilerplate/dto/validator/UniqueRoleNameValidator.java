package com.backend.boilerplate.dto.validator;

import com.backend.boilerplate.dao.RoleRepository;
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
public class UniqueRoleNameValidator implements ConstraintValidator<UniqueField, String> {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    @SuppressWarnings("squid:S3655")
    public boolean isValid(String roleName, ConstraintValidatorContext context) {
        Optional<Long> longOptional = roleRepository.countByNameIgnoreCase(roleName);
        return longOptional.get() == 0;
    }
}