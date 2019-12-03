package com.backend.boilerplate.dao;

import com.backend.boilerplate.entity.Claim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author sarvesh
 * @version 0.0.1
 * @since 0.0.1
 */
@Repository
public interface ClaimRepository extends JpaRepository<Claim, Long> {
    Optional<Claim> findByUuid(UUID uuid);

    Optional<Long> countByUuid(UUID uuid);

    Optional<Long> countByUuidIn(List<UUID> uuids);
}
