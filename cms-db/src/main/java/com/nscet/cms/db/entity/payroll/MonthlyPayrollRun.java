package com.nscet.cms.db.entity.payroll;

import com.nscet.cms.db.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "payroll_monthly_run")
public class MonthlyPayrollRun extends BaseEntity {

    @Column(name = "pay_period", nullable = false, length = 20)
    private String payPeriod;

    @Column(name = "staff_code", nullable = false, length = 30)
    private String staffCode;

    @Column(name = "staff_name", length = 100)
    private String staffName;

    @Column(name = "department", length = 100)
    private String department;

    @Column(name = "working_days")
    private Integer workingDays = 30;

    @Column(name = "paid_days")
    private Integer paidDays = 30;

    @Column(name = "lop_days")
    private Integer lopDays = 0;

    @Column(name = "basic_pay", precision = 12, scale = 2)
    private BigDecimal basicPay = BigDecimal.ZERO;

    @Column(name = "special_allowance", precision = 12, scale = 2)
    private BigDecimal specialAllowance = BigDecimal.ZERO;

    @Column(name = "hra", precision = 12, scale = 2)
    private BigDecimal hra = BigDecimal.ZERO;

    @Column(name = "conveyance", precision = 12, scale = 2)
    private BigDecimal conveyance = BigDecimal.ZERO;

    @Column(name = "washing_allowance", precision = 12, scale = 2)
    private BigDecimal washingAllowance = BigDecimal.ZERO;

    @Column(name = "gross_pay", precision = 12, scale = 2)
    private BigDecimal grossPay = BigDecimal.ZERO;

    @Column(name = "lop_deduction", precision = 12, scale = 2)
    private BigDecimal lopDeduction = BigDecimal.ZERO;

    @Column(name = "epf_deduction", precision = 12, scale = 2)
    private BigDecimal epfDeduction = BigDecimal.ZERO;

    @Column(name = "esi_deduction", precision = 12, scale = 2)
    private BigDecimal esiDeduction = BigDecimal.ZERO;

    @Column(name = "income_tax", precision = 12, scale = 2)
    private BigDecimal incomeTax = BigDecimal.ZERO;

    @Column(name = "professional_tax", precision = 12, scale = 2)
    private BigDecimal professionalTax = BigDecimal.ZERO;

    @Column(name = "staff_club", precision = 12, scale = 2)
    private BigDecimal staffClub = BigDecimal.ZERO;

    @Column(name = "total_deductions", precision = 12, scale = 2)
    private BigDecimal totalDeductions = BigDecimal.ZERO;

    @Column(name = "net_pay", precision = 12, scale = 2)
    private BigDecimal netPay = BigDecimal.ZERO;
}
