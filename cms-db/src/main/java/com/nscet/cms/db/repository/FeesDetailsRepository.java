package com.nscet.cms.db.repository;

import com.nscet.cms.db.entity.FeesDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface FeesDetailsRepository extends JpaRepository<FeesDetails, Long> {

    @Query("SELECT fd FROM FeesDetails fd WHERE fd.isActive = true")
    Page<FeesDetails> findAllActive(Pageable pageable);

    @Query("SELECT fd FROM FeesDetails fd WHERE fd.isActive = true " +
           "AND (LOWER(fd.degree) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(fd.admissionType) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(fd.deptType) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<FeesDetails> search(@Param("search") String search, Pageable pageable);

    @Query("SELECT fd FROM FeesDetails fd WHERE fd.isActive = true " +
           "AND ((fd.fromDate >= :fromDate AND fd.fromDate <= :toDate) " +
           "OR (fd.toDate >= :fromDate AND fd.toDate <= :toDate))")
    List<FeesDetails> findByDateRange(@Param("fromDate") LocalDate fromDate,
                                      @Param("toDate") LocalDate toDate);

    @Query("SELECT fd FROM FeesDetails fd WHERE fd.isActive = true " +
           "AND (:semester IS NULL OR fd.semester = :semester) " +
           "AND (:degree IS NULL OR fd.degree = :degree) " +
           "AND (:quotaId IS NULL OR fd.quota.id = :quotaId)")
    List<FeesDetails> findByCriteria(@Param("semester") Integer semester,
                                     @Param("degree") String degree,
                                     @Param("quotaId") Long quotaId);

    @Query("SELECT fd FROM FeesDetails fd WHERE fd.isActive = true " +
           "AND fd.department.id = :deptId AND fd.semester = :semester " +
           "AND fd.quota.id = :quotaId AND fd.admissionType = :admissionType")
    List<FeesDetails> findFeeStructure(@Param("deptId") Long deptId,
                                       @Param("semester") Integer semester,
                                       @Param("quotaId") Long quotaId,
                                       @Param("admissionType") String admissionType);

    @Query("SELECT COALESCE(SUM(fd.amount), 0) FROM FeesDetails fd WHERE fd.isActive = true")
    Optional<BigDecimal> sumAmount();
}
