package com.nscet.cms.db.repository.payroll;

import com.nscet.cms.db.entity.payroll.MonthlyPayrollRun;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MonthlyPayrollRunRepository extends JpaRepository<MonthlyPayrollRun, Long> {

    @Query("SELECT m FROM MonthlyPayrollRun m WHERE m.isActive = true AND m.payPeriod = :payPeriod ORDER BY m.staffCode ASC")
    List<MonthlyPayrollRun> findByPayPeriod(@Param("payPeriod") String payPeriod);

    @Query("SELECT m FROM MonthlyPayrollRun m WHERE m.isActive = true ORDER BY m.id DESC")
    List<MonthlyPayrollRun> findAllActive();
}
