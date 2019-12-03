package com.backend.boilerplate.service.impl;

import com.backend.boilerplate.config.ModelMapperConfig;
import com.backend.boilerplate.dao.ClaimRepository;
import com.backend.boilerplate.dto.ClaimDto;
import com.backend.boilerplate.entity.Claim;
import com.backend.boilerplate.modelmapper.ClaimMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author sarvesh
 * @version 0.0.1
 * @since 0.0.1
 */
@ExtendWith(SpringExtension.class)
@Import({ClaimMapper.class, ModelMapper.class, ModelMapperConfig.class})
public class ClaimServiceImplTest {

    private ClaimServiceImpl claimServiceImpl;

    @Mock
    private ClaimRepository claimRepositoryMock;

    @Mock
    private PlatformTransactionManager platformTransactionManagerMock;

    @Autowired
    private ClaimMapper claimMapper;

    /**
     * Setup for before each test case
     */
    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        claimServiceImpl = new ClaimServiceImpl(platformTransactionManagerMock);

        ReflectionTestUtils.setField(claimServiceImpl, "claimRepository", claimRepositoryMock);
        ReflectionTestUtils.setField(claimServiceImpl, "claimMapper", claimMapper);
    }

    @Test
    void getAllClaims_ShouldPass_WithData() {
        /*********** Setup ************/
        UUID claimId = UUID.randomUUID();
        List<Claim> claimList = new ArrayList<>();
        Claim claim = prepareClaim(claimId, "UserGetAll");
        claimList.add(claim);
        Mockito.when(claimRepositoryMock.findAll()).thenReturn(claimList);

        /*********** Execute ************/
        List<ClaimDto> claimDtos = claimServiceImpl.getAllClaims();

        /*********** Verify/Assertions ************/
        Mockito.verify(claimRepositoryMock, Mockito.times(1)).findAll();
        assertNotNull(claimDtos);
        assertEquals(1, claimDtos.size());
        assertEquals(claimId.toString(), claimDtos.get(0).getUuid().toString());
        assertEquals(claim.getResourceName(), claimDtos.get(0).getResourceName());
    }

    @Test
    void getAllClaims_ShouldPass_NoData() {
        /*********** Setup ************/
        UUID claimId = UUID.randomUUID();
        Mockito.when(claimRepositoryMock.findAll()).thenReturn(new ArrayList<>());

        /*********** Execute ************/
        List<ClaimDto> claimDtos = claimServiceImpl.getAllClaims();

        /*********** Verify/Assertions ************/
        Mockito.verify(claimRepositoryMock, Mockito.times(1)).findAll();
        assertNotNull(claimDtos);
        assertEquals(0, claimDtos.size());
    }

    private Claim prepareClaim(UUID claimId, String claimName) {
        Claim claim = new Claim();
        claim.setUuid(claimId);
        claim.setResourceName(claimName);
        return claim;
    }

}
