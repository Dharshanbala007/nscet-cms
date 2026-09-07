package com.nscet.cms.db.repository.payroll;

import com.nscet.cms.db.entity.payroll.LatePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LatePermissionRepository extends JpaRepository<LatePermission, Long> {

    @Query("SELECT lp FROM LatePermission lp WHERE lp.isActive = true ORDER BY lp.permissionDate DESC, lp.id DESC")
    List<LatePermission> findAllActive();

    @Query("SELECT lp FROM LatePermission lp WHERE lp.isActive = true AND lp.permissionDate BETWEEN :startDate AND :endDate ORDER BY lp.permissionDate DESC")
    List<LatePermission> findByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT lp FROM LatePermission lp WHERE lp.isActive = true AND lp.staffCode = :staffCode ORDER BY lp.permissionDate DESC")
    List<LatePermission> findByStaffCode(@Param("staffCode") String staffCode);
}
