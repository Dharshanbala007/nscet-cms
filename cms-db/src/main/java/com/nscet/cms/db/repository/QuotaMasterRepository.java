package com.nscet.cms.db.repository;

import com.nscet.cms.db.entity.QuotaMaster;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface QuotaMasterRepository extends JpaRepository<QuotaMaster, Long> {

    @Query("SELECT q FROM QuotaMaster q WHERE q.isActive = true")
    Page<QuotaMaster> findAllActive(Pageable pageable);

    @Query("SELECT q FROM QuotaMaster q WHERE q.isActive = true " +
           "AND (LOWER(q.code) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(q.name) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<QuotaMaster> search(@Param("search") String search, Pageable pageable);
}
