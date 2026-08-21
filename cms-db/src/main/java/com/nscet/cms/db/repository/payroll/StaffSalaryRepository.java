package com.nscet.cms.db.repository.payroll;

import com.nscet.cms.db.entity.payroll.StaffSalary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StaffSalaryRepository extends JpaRepository<StaffSalary, Long> {

    @Query("SELECT s FROM StaffSalary s WHERE s.isActive = true ORDER BY s.staffCode ASC")
    List<StaffSalary> findAllActive();

    Optional<StaffSalary> findByStaffCode(String staffCode);

    @Query("SELECT s FROM StaffSalary s WHERE s.isActive = true AND " +
           "(LOWER(s.staffCode) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(s.staffName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(s.department) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<StaffSalary> search(@Param("query") String query);
}
