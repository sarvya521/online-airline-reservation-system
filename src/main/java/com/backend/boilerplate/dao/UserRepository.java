package com.backend.boilerplate.dao;

import com.backend.boilerplate.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * @author sarvesh
 * @version 0.0.1
 * @since 0.0.1
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u.id FROM User u WHERE u.uuid = (:uuid)")
    Optional<Long> findIdByUuid(@Param("uuid") UUID uuid);

    Optional<User> findByUuid(UUID uuid);

    Optional<Long> countByEmailIgnoreCase(String email);

    Optional<Long> countByUuid(UUID uuid);

    Optional<Long> countByUuidNotAndEmailIgnoreCase(UUID uuid, String email);
}
