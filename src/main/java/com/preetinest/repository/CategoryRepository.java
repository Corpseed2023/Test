package com.preetinest.repository;

import com.preetinest.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByUuid(String uuid);

    Optional<Category> findBySlug(String slug);

    @Query("SELECT c FROM Category c WHERE c.deleteStatus = 2 AND c.active = true")
    List<Category> findAllActiveCategories();
}