package com.gfg.userservice.repository;

import com.gfg.userservice.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User,Integer> {
    Optional<User> findByCredentialUsername(final String username);

    User findUserByUserId(Integer userId);

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    @Query("SELECT COUNT(c) FROM Credential c WHERE c.isEnabled = true AND c.isAccountNonLocked = true")
    long countUser();

    @Query("SELECT u FROM User u JOIN FETCH u.credential c")
    List<User> findAllUsersWithAccountStatus();

}
