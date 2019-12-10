package com.oars.dto.validator;

import com.oars.dao.UserRepository;
import com.oars.dto.UpdateUserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Optional;
import java.util.UUID;


@Component
public class UniqueUserValidator implements ConstraintValidator<UniqueResource, UpdateUserDto> {

    @Autowired
    private UserRepository userRepository;

    @Override
    @SuppressWarnings("squid:S3655")
    public boolean isValid(UpdateUserDto updateUserDto, ConstraintValidatorContext context) {
        UUID uuid = updateUserDto.getUuid();
        Optional<Long> countExistOptional = userRepository.countByUuid(uuid);
        if (countExistOptional.get() < 1) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("1004")
                .addPropertyNode("uuid")
                .addConstraintViolation();
            return false;
        }

        Optional<Long> countEmailOptional =
            userRepository.countByUuidNotAndEmailIgnoreCase(uuid, updateUserDto.getEmail());
        if (countEmailOptional.get() > 0) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("1061")
                .addPropertyNode("email")
                .addConstraintViolation();
            return false;
        }

        return true;
    }
}