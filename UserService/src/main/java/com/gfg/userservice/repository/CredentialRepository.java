package com.gfg.userservice.repository;

import com.gfg.userservice.domain.entity.Credential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CredentialRepository extends JpaRepository<Credential,Integer> {
    Optional<Credential> findByUsername(final String username);

    boolean existsByUsername(String username);

}
