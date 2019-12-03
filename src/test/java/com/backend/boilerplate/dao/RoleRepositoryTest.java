package com.backend.boilerplate.dao;

import com.backend.boilerplate.TestBoilerplateServiceApplication;
import com.backend.boilerplate.entity.Claim;
import com.backend.boilerplate.entity.Role;
import com.backend.boilerplate.entity.RoleClaim;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashSet;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static com.backend.boilerplate.entity.Status.CREATED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author sarvesh
 * @version 0.0.2
 * @since 0.0.1
 */
@ExtendWith(SpringExtension.class)
@DataJpaTest
@ContextConfiguration(classes = {TestBoilerplateServiceApplication.class})
@AutoConfigureEmbeddedDatabase
@ActiveProfiles("embeddedpostgres")
class RoleRepositoryTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private RoleRepository roleRepository;

    private Role role;

    private Set<Claim> claims = new HashSet<>();

    private static final Long PERFORMED_BY = -1L;

    @BeforeEach
    public void setup() {
        Claim userGetClaim = Claim.builder()
            .resourceName("UserGetAll")
            .resourceHttpMethod("GET")
            .resourceEndpoint("/api/v1/user")
            .status(CREATED)
            .performedBy(PERFORMED_BY)
            .build();
        userGetClaim = testEntityManager.persistAndFlush(userGetClaim);
        Claim userPostClaim = Claim.builder()
            .resourceName("UserCreate")
            .resourceHttpMethod("POST")
            .resourceEndpoint("/api/v1/user")
            .status(CREATED)
            .performedBy(PERFORMED_BY)
            .build();

        userPostClaim = testEntityManager.persistAndFlush(userPostClaim);
        claims.add(userGetClaim);
        claims.add(userPostClaim);

        role = Role.builder()
            .name("Manager")
            .status(CREATED)
            .performedBy(PERFORMED_BY)
            .build();

        Role finalRole = role;
        Set<RoleClaim> roleClaims = claims.stream()
            .map(claim -> new RoleClaim(finalRole, claim, PERFORMED_BY))
            .collect(Collectors.toSet());
        role.setRoleClaims(roleClaims);
    }

    @Test
    public void saveRole_shouldPass_withClaims() {
        /*********** Execute ************/
        role = roleRepository.saveAndFlush(role);

        /*********** Verify/Assertions ************/
        assertNotNull(role.getId());
        assertEquals(claims.size(), role.getRoleClaims().size());
        assertNotNull(role.getRoleClaims().stream().findFirst().get().getRole().getId());
    }

    @Test
    public void updateRole_shouldPass_withNewClaimsAddedAndOldClaimsRemoved() {
        /*********** Setup ************/
        role = roleRepository.saveAndFlush(role);
        Set<Claim> newClaims = prepareNewClaims();
        Set<RoleClaim> roleClaims = newClaims.stream()
            .map(claim -> new RoleClaim(role, claim, PERFORMED_BY))
            .collect(Collectors.toSet());
        role.getRoleClaims().clear();
        role.getRoleClaims().addAll(roleClaims);

        /*********** Execute ************/
        role = roleRepository.saveAndFlush(role);

        Role actualRole = roleRepository.getOne(role.getId());

        /*********** Verify/Assertions ************/
        assertNotNull(actualRole.getId());
        assertEquals(newClaims.size(), actualRole.getRoleClaims().size());
        assertNotNull(actualRole.getRoleClaims().stream().findFirst().get().getRole().getId());
        assertTrue(actualRole.getRoleClaims().containsAll(roleClaims));
    }

    private Set<Claim> prepareNewClaims() {
        Claim userUpdateClaim = Claim.builder()
            .resourceName("UserUpdate")
            .resourceHttpMethod("PUT")
            .resourceEndpoint("/api/v1/user")
            .status(CREATED)
            .performedBy(PERFORMED_BY)
            .build();
        userUpdateClaim = testEntityManager.persistAndFlush(userUpdateClaim);
        Claim userDeleteClaim = Claim.builder()
            .resourceName("UserDelete")
            .resourceHttpMethod("DELETE")
            .resourceEndpoint("/api/v1/user")
            .status(CREATED)
            .performedBy(PERFORMED_BY)
            .build();
        userDeleteClaim = testEntityManager.persistAndFlush(userDeleteClaim);

        return Set.of(userUpdateClaim, userDeleteClaim);
    }
}
