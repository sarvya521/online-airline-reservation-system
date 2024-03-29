package com.oars.dao;

import com.oars.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<Long> countByEmailIgnoreCase(String email);

    Optional<Long> countByIdNotAndEmailIgnoreCase(Long id, String email);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findByRole(String role);

    List<User> findByRoleAndBookingsNotEmpty(String role);
}
