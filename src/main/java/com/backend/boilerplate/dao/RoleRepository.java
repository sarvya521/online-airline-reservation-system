package com.backend.boilerplate.dao;

import com.backend.boilerplate.entity.Role;
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
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByUuid(UUID uuid);

    Optional<Role> findByNameIgnoreCase(String name);

    List<Role> findAllByUuidIn(Iterable<UUID> uuids);

    Optional<Long> countByNameIgnoreCase(String name);

    Optional<Long> countByUuid(UUID uuid);

    Optional<Long> countByUuidNotAndNameIgnoreCase(UUID uuid, String name);

    List<Role> findByNameIn(List<String> names);
}
