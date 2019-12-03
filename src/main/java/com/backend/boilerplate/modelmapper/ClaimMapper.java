package com.backend.boilerplate.modelmapper;

import com.backend.boilerplate.dto.ClaimDto;
import com.backend.boilerplate.entity.Claim;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Claim Mapper to map {@link ClaimDto} to {@link Claim} and vice-versa
 *
 * @author sarvesh
 * @version 0.0.1
 * @since 0.0.1
 */
@Component
public class ClaimMapper {

    @Autowired
    private ModelMapper modelMapper;

    /**
     * Convert {@link Claim} to {@link ClaimDto}
     *
     * @param claim
     * @return {@link ClaimDto}
     */
    public ClaimDto convertToDto(Claim claim) {
        return modelMapper.map(claim, ClaimDto.class);
    }

    /**
     * Convert {@link ClaimDto} to {@link Claim}
     *
     * @param claimDto
     * @return {@link Claim}
     */
    public Claim convertToEntity(ClaimDto claimDto) {
        return modelMapper.map(claimDto, Claim.class);
    }

    /**
     * Convert List of {@link Claim} to List of {@link ClaimDto}
     *
     * @param claims
     * @return
     */
    public List<ClaimDto> convertToDtos(List<Claim> claims) {
        return claims.stream().map(claim -> convertToDto(claim)).collect(Collectors.toList());
    }

    /**
     * Merge {@link ClaimDto} with {@link Claim}
     *
     * @param claimDto
     * @param claim
     */
    public void mergeToEntity(ClaimDto claimDto, Claim claim) {
        modelMapper.map(claimDto, claim);
    }

    public ClaimDto convertToClaimDto(Claim claim) {
        return modelMapper.map(claim, ClaimDto.class);
    }
}
