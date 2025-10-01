package com.preetinest.repository;

import com.preetinest.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUuid(String uuid);

    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.deleteStatus = 2 AND u.isEnable = true")
    List<User> findAllActiveUsers();
}