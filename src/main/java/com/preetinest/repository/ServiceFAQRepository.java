package com.preetinest.repository;

import com.preetinest.entity.ServiceFAQ;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceFAQRepository extends JpaRepository<ServiceFAQ, Long> {
    Optional<ServiceFAQ> findByUuid(String uuid);

    @Query("SELECT sf FROM ServiceFAQ sf WHERE sf.service.id = :serviceId AND sf.deleteStatus = 2 ORDER BY sf.displayOrder ASC")
    List<ServiceFAQ> findByServiceId(@Param("serviceId") Long serviceId);
}