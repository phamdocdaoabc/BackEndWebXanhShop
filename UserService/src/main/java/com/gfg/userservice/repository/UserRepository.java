package com.gfg.userservice.repository;

import com.gfg.userservice.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User,Integer> {
    Optional<User> findByCredentialUsername(final String username);

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

}
