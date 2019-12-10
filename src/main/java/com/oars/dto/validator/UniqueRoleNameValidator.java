package com.oars.dto.validator;

import com.oars.dao.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Optional;


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