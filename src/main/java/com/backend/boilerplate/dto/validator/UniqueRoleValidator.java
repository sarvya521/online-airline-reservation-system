package com.backend.boilerplate.dto.validator;

import com.backend.boilerplate.dao.ClaimRepository;
import com.backend.boilerplate.dao.RoleRepository;
import com.backend.boilerplate.dto.UpdateRoleDto;
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
public class UniqueRoleValidator implements ConstraintValidator<UniqueResource, UpdateRoleDto> {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ClaimRepository claimRepository;

    @Override
    @SuppressWarnings("squid:S3655")
    public boolean isValid(UpdateRoleDto updateRoleDto, ConstraintValidatorContext context) {
        UUID uuid = updateRoleDto.getUuid();
        Optional<Long> countExistOptional = roleRepository.countByUuid(uuid);
        if (countExistOptional.get() < 1) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("1009")
                .addPropertyNode("uuid")
                .addConstraintViolation();
            return false;
        }

        Optional<Long> countNameOptional =
            roleRepository.countByUuidNotAndNameIgnoreCase(uuid, updateRoleDto.getName());
        if (countNameOptional.get() > 0) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("1008")
                .addPropertyNode("name")
                .addConstraintViolation();
            return false;
        }

        Optional<Long> countClaimOptional = claimRepository.countByUuidIn(updateRoleDto.getClaims());
        if (countClaimOptional.get() < 1 || countClaimOptional.get() != updateRoleDto.getClaims().size()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("1015")
                .addPropertyNode("claims")
                .addConstraintViolation();
            return false;
        }

        return true;
    }
}