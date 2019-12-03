package com.backend.boilerplate.dao;

import com.backend.boilerplate.TestBoilerplateServiceApplication;
import com.backend.boilerplate.entity.Claim;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.backend.boilerplate.entity.Status.CREATED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author sarvesh
 * @version 0.0.1
 * @since 0.0.1
 */
@ExtendWith(SpringExtension.class)
@DataJpaTest
@ContextConfiguration(classes = {TestBoilerplateServiceApplication.class})
@AutoConfigureEmbeddedDatabase
@ActiveProfiles("embeddedpostgres")
public class ClaimRepositoryTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private ClaimRepository claimRepository;

    private List<Claim> claimList = new ArrayList<>();

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
        claimList.add(userGetClaim);
        claimList.add(userPostClaim);
    }

    @Test
    void findAll_shouldPass() {
        /*********** Execute ************/
        List<Claim> actualClaims = claimRepository.findAll();

        /*********** Verify/Assertions ************/
        assertNotNull(actualClaims);
        assertEquals(2, actualClaims.size());
        assertTrue(actualClaims.containsAll(claimList));
    }

    @Test
    void findByUuid_shouldPass() {
        /*********** Setup ************/
        UUID claimUuid = claimList.get(0).getUuid();

        /*********** Execute ************/
        Optional<Claim> actualClaim = claimRepository.findByUuid(claimUuid);

        /*********** Verify/Assertions ************/
        assertNotNull(actualClaim);
        assertEquals(claimList.get(0), actualClaim.get());
    }
}
