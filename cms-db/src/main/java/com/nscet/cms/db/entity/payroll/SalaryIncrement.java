package com.nscet.cms.db.entity.payroll;

import com.nscet.cms.db.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "payroll_increments")
public class SalaryIncrement extends BaseEntity {

    @Column(name = "staff_code", nullable = false, length = 30)
    private String staffCode;

    @Column(name = "staff_name", length = 100)
    private String staffName;

    @Column(name = "department", length = 100)
    private String department;

    @Column(name = "effective_date", nullable = false)
    private LocalDate effectiveDate;

    @Column(name = "old_basic", precision = 12, scale = 2)
    private BigDecimal oldBasic = BigDecimal.ZERO;

    @Column(name = "new_basic", precision = 12, scale = 2)
    private BigDecimal newBasic = BigDecimal.ZERO;

    @Column(name = "old_special_allowance", precision = 12, scale = 2)
    private BigDecimal oldSpecialAllowance = BigDecimal.ZERO;

    @Column(name = "new_special_allowance", precision = 12, scale = 2)
    private BigDecimal newSpecialAllowance = BigDecimal.ZERO;

    @Column(name = "increment_amount", precision = 12, scale = 2)
    private BigDecimal incrementAmount = BigDecimal.ZERO;

    @Column(name = "new_gross", precision = 12, scale = 2)
    private BigDecimal newGross = BigDecimal.ZERO;

    @Column(name = "remarks", length = 255)
    private String remarks;
}
