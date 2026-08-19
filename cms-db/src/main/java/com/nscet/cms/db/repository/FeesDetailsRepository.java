package com.nscet.cms.db.repository;

import com.nscet.cms.db.entity.FeesDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface FeesDetailsRepository extends JpaRepository<FeesDetails, Long> {

    @Query("SELECT fd FROM FeesDetails fd WHERE fd.isActive = true " +
           "AND fd.department.id = :deptId AND fd.semester = :semester " +
           "AND fd.quota.id = :quotaId AND fd.admissionType = :admissionType")
    List<FeesDetails> findFeeStructure(@Param("deptId") Long deptId,
                                       @Param("semester") Integer semester,
                                       @Param("quotaId") Long quotaId,
                                       @Param("admissionType") String admissionType);

    @Query("SELECT COALESCE(SUM(fd.amount), 0) FROM FeesDetails fd WHERE fd.isActive = true")
    Optional<BigDecimal> sumAmount();

    @Query("SELECT fd FROM FeesDetails fd WHERE fd.isActive = true")
    Page<FeesDetails> findAllActive(Pageable pageable);

    @Query("SELECT fd FROM FeesDetails fd WHERE fd.isActive = true")
    List<FeesDetails> findAllActiveList();

    @Query("SELECT fd FROM FeesDetails fd WHERE fd.isActive = true " +
           "AND (LOWER(fd.feesName.name) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(fd.department.name) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<FeesDetails> search(@Param("search") String search, Pageable pageable);
}
