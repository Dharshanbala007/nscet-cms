package com.nscet.cms.db.repository.payroll;

import com.nscet.cms.db.entity.payroll.SalaryIncrement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SalaryIncrementRepository extends JpaRepository<SalaryIncrement, Long> {

    @Query("SELECT i FROM SalaryIncrement i WHERE i.isActive = true ORDER BY i.effectiveDate DESC")
    List<SalaryIncrement> findAllActive();
}
