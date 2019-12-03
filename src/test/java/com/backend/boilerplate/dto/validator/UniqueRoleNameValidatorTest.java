package com.backend.boilerplate.dto.validator;

import com.backend.boilerplate.TestBoilerplateServiceApplication;
import com.backend.boilerplate.config.ErrorMessageSourceConfig;
import com.backend.boilerplate.dto.CreateRoleDto;
import com.backend.boilerplate.entity.Role;
import com.backend.boilerplate.util.ErrorGeneratorInitializer;
import com.backend.boilerplate.web.exception.CommonResponseEntityExceptionHandler;
import com.backend.boilerplate.web.exception.UserManagementExceptionHandler;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.SpringConstraintValidatorFactory;

import javax.validation.ConstraintViolation;
import java.util.Set;
import java.util.UUID;

import static com.backend.boilerplate.entity.Status.CREATED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author sarvesh
 * @version 0.0.1
 * @since 0.0.1
 */
@ExtendWith(SpringExtension.class)
@DataJpaTest
@ContextConfiguration(classes = {TestBoilerplateServiceApplication.class})
@Import({
    SpringConstraintValidatorFactory.class,
    LocalValidatorFactoryBean.class,
    ErrorMessageSourceConfig.class,
    ErrorGeneratorInitializer.class,
    UserManagementExceptionHandler.class,
    CommonResponseEntityExceptionHandler.class})
@AutoConfigureEmbeddedDatabase
@ActiveProfiles("embeddedpostgres")
public class UniqueRoleNameValidatorTest {

    @Autowired
    private LocalValidatorFactoryBean validator;

    @Autowired
    private TestEntityManager testEntityManager;

    private Role role;

    @BeforeEach
    void setup() {
        role = new Role();
        role.setName("Manager");
        role.setStatus(CREATED);
        role.setPerformedBy(1L);
        role = testEntityManager.persistAndFlush(role);
    }

    @Test
    void createRole_shouldPass_noDuplicateName() {
        CreateRoleDto dto = prepareCreateRoleDto("Team Lead");
        Set<ConstraintViolation<CreateRoleDto>> constraintViolations = validator.validateProperty(dto, "name",
            Extended.class);
        assertTrue(constraintViolations.isEmpty());
    }

    @Test
    void createRole_shouldThrow_duplicateName() {
        CreateRoleDto dto = prepareCreateRoleDto("Manager");
        Set<ConstraintViolation<CreateRoleDto>> constraintViolations = validator.validateProperty(dto, "name",
            Extended.class);
        assertFalse(constraintViolations.isEmpty());
        assertThat(constraintViolations).hasSize(1);
        assertThat(constraintViolations.stream().findFirst().get().getMessage()).isEqualTo("1008");
    }

    private CreateRoleDto prepareCreateRoleDto(String roleName) {
        CreateRoleDto dto = new CreateRoleDto();
        dto.setName(roleName);
        dto.getClaims().add(UUID.randomUUID());
        return dto;
    }
}
