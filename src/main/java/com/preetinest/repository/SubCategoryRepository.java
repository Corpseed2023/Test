package com.preetinest.repository;


import com.preetinest.entity.SubCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SubCategoryRepository extends JpaRepository<SubCategory, Long> {
    Optional<SubCategory> findByUuid(String uuid);

    Optional<SubCategory> findBySlug(String slug);

    @Query("SELECT sc FROM SubCategory sc WHERE sc.deleteStatus = 2 AND sc.active = true")
    List<SubCategory> findAllActiveSubCategories();
}