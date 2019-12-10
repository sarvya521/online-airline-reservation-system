package com.oars.service.impl;

import com.oars.dao.ClaimRepository;
import com.oars.dto.ClaimDto;
import com.oars.entity.Claim;
import com.oars.modelmapper.ClaimMapper;
import com.oars.service.ClaimService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

/**
 * Implementation of {@link ClaimService}.
 *
 * @author sarvesh
 * @version 0.0.1
 * @since 0.0.1
 */
@Service
public class ClaimServiceImpl implements ClaimService {

    @Autowired
    private ClaimRepository claimRepository;

    @Autowired
    private ClaimMapper claimMapper;

    private final TransactionTemplate transactionTemplate;

    public ClaimServiceImpl(PlatformTransactionManager transactionManager) {
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    @Override
    public List<ClaimDto> getAllClaims() {
        List<Claim> claims = claimRepository.findAll();
        return claimMapper.convertToDtos(claims);
    }
}
