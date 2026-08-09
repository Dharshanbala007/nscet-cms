package com.nscet.cms.db.repository;

import com.nscet.cms.db.entity.DesignationMaster;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DesignationMasterRepository extends JpaRepository<DesignationMaster, Long> {

    boolean existsByCode(String code);

    boolean existsByCodeAndIdNot(String code, Long id);

    Optional<DesignationMaster> findByCode(String code);

    @Query("SELECT d FROM DesignationMaster d WHERE d.isActive = true")
    List<DesignationMaster> findAllActiveList();

    @Query("SELECT d FROM DesignationMaster d WHERE d.isActive = true")
    Page<DesignationMaster> findAllActive(Pageable pageable);

    @Query("SELECT d FROM DesignationMaster d WHERE d.isActive = true " +
           "AND (LOWER(d.code) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(d.name) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(d.shortName) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<DesignationMaster> search(@Param("search") String search, Pageable pageable);
}
