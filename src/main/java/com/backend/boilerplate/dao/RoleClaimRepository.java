package com.backend.boilerplate.dao;

import com.backend.boilerplate.entity.RoleClaim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author sarvesh
 * @version 0.0.2
 * @since 0.0.1
 * @deprecated
 */
@Deprecated(since = "0.0.2", forRemoval = true)
@Repository
public interface RoleClaimRepository extends JpaRepository<RoleClaim, RoleClaim.RoleClaimId> {
}
