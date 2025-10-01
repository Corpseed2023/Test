package com.preetinest.repository;

import com.preetinest.entity.Services;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceRepository extends JpaRepository<Services, Long> {
    Optional<Services> findByUuid(String uuid);

    Optional<Services> findBySlug(String slug);

    @Query("SELECT s FROM Services s WHERE s.deleteStatus = 2 AND s.active = true")
    List<Services> findAllActiveServices();
}