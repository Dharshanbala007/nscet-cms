package com.nscet.cms.db.repository;

import com.nscet.cms.db.entity.FeesDetails;
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
}
