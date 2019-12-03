package com.backend.boilerplate.dao;

import com.backend.boilerplate.entity.UserHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * @author sarvesh
 * @version 0.0.1
 * @since 0.0.1
 */
@Repository
public interface UserHistoryRepository extends JpaRepository<UserHistory, Long> {

    Optional<UserHistory> findByUuid(UUID uuid);
}
