package com.backend.boilerplate.dto.validator;

import com.backend.boilerplate.TestBoilerplateServiceApplication;
import com.backend.boilerplate.config.ErrorMessageSourceConfig;
import com.backend.boilerplate.dto.UpdateRoleDto;
import com.backend.boilerplate.entity.Claim;
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
import java.util.ArrayList;
import java.util.List;
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
public class UniqueRoleValidatorTest {

    @Autowired
    private LocalValidatorFactoryBean validator;

    @Autowired
    private TestEntityManager testEntityManager;

    private List<Role> roles = new ArrayList<>();
    private List<Claim> claims = new ArrayList<>();

    private static final Long PERFORMED_BY = -1L;

    @BeforeEach
    void setup() {
        Role manager = Role.builder()
            .name("Manager")
            .status(CREATED)
            .performedBy(PERFORMED_BY)
            .build();
        manager = testEntityManager.persistAndFlush(manager);

        Role teamLead = Role.builder()
            .name("Team Lead")
            .status(CREATED)
            .performedBy(PERFORMED_BY)
            .build();
        teamLead = testEntityManager.persistAndFlush(teamLead);

        roles.add(manager);
        roles.add(teamLead);

        Claim userGetClaim = Claim.builder()
            .resourceName("UserGetAll")
            .resourceHttpMethod("GET")
            .resourceEndpoint("/api/v1/user")
            .status(CREATED)
            .performedBy(PERFORMED_BY)
            .build();
        userGetClaim = testEntityManager.persistAndFlush(userGetClaim);

        claims.add(userGetClaim);
    }

    @Test
    void updateRole_shouldPass() {
        UUID uuid = roles.get(0).getUuid();
        UpdateRoleDto dto = prepareUpdateRoleDto(uuid, "Senior Manager");
        Set<ConstraintViolation<UpdateRoleDto>> constraintViolations = validator.validate(dto, Extended.class);
        assertTrue(constraintViolations.isEmpty());
    }

    @Test
    void updateRole_shouldThrow_roleNotFound() {
        UUID uuid = UUID.randomUUID();
        UpdateRoleDto dto = prepareUpdateRoleDto(uuid, "Team Lead");
        Set<ConstraintViolation<UpdateRoleDto>> constraintViolations = validator.validate(dto, Extended.class);
        assertFalse(constraintViolations.isEmpty());
        assertThat(constraintViolations).hasSize(1);
        assertThat(constraintViolations.stream().findFirst().get().getMessage()).isEqualTo("1009");
    }

    @Test
    void updateRole_shouldThrow_duplicateName() {
        UUID uuid = roles.get(0).getUuid();
        UpdateRoleDto dto = prepareUpdateRoleDto(uuid, "Team Lead");
        Set<ConstraintViolation<UpdateRoleDto>> constraintViolations = validator.validate(dto, Extended.class);
        assertFalse(constraintViolations.isEmpty());
        assertThat(constraintViolations).hasSize(1);
        assertThat(constraintViolations.stream().findFirst().get().getMessage()).isEqualTo("1008");
    }

    private UpdateRoleDto prepareUpdateRoleDto(UUID roleUuid, String roleName) {
        UpdateRoleDto dto = new UpdateRoleDto();
        dto.setUuid(roleUuid);
        dto.setName(roleName);
        dto.getClaims().add(claims.get(0).getUuid());
        return dto;
    }
}