package com.preetinest.repository;

import com.preetinest.entity.ServiceDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceDetailRepository extends JpaRepository<ServiceDetail, Long> {
    Optional<ServiceDetail> findByUuid(String uuid);

    @Query("SELECT sd FROM ServiceDetail sd WHERE sd.service.id = :serviceId AND sd.deleteStatus = 2")
    List<ServiceDetail> findByServiceId(@Param("serviceId") Long serviceId);
}