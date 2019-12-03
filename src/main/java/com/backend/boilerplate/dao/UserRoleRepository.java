package com.backend.boilerplate.dao;

import com.backend.boilerplate.entity.Role;
import com.backend.boilerplate.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author sarvesh
 * @version 0.0.2
 * @since 0.0.1
 * @deprecated
 */
@Deprecated(since = "0.0.2", forRemoval = true)
@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, UserRole.UserRoleId> {

    @Query("FROM UserRole WHERE id.role =:role")
    List<UserRole> findByRole(@Param("role") Role role);
}
