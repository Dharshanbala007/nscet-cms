package com.nscet.cms.db.entity.payroll;

import com.nscet.cms.db.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "payroll_staff_salary")
public class StaffSalary extends BaseEntity {

    @Column(name = "staff_code", nullable = false, unique = true, length = 30)
    private String staffCode;

    @Column(name = "staff_name", nullable = false, length = 100)
    private String staffName;

    @Column(name = "department", length = 100)
    private String department;

    @Column(name = "designation", length = 100)
    private String designation;

    @Column(name = "category", length = 50)
    private String category = "Teaching";

    @Column(name = "bank_name", length = 100)
    private String bankName;

    @Column(name = "bank_acc_no", length = 50)
    private String bankAccNo;

    @Column(name = "basic_pay", precision = 12, scale = 2)
    private BigDecimal basicPay = BigDecimal.ZERO;

    @Column(name = "special_allowance", precision = 12, scale = 2)
    private BigDecimal specialAllowance = BigDecimal.ZERO;

    @Column(name = "hra", precision = 12, scale = 2)
    private BigDecimal hra = BigDecimal.ZERO;

    @Column(name = "ta_type", length = 20)
    private String taType = "MONTHLY";

    @Column(name = "ta_amount", precision = 12, scale = 2)
    private BigDecimal taAmount = BigDecimal.ZERO;

    @Column(name = "washing_allowance", precision = 12, scale = 2)
    private BigDecimal washingAllowance = BigDecimal.ZERO;

    @Column(name = "conveyance", precision = 12, scale = 2)
    private BigDecimal conveyance = BigDecimal.ZERO;

    @Column(name = "gross_salary", precision = 12, scale = 2)
    private BigDecimal grossSalary = BigDecimal.ZERO;

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

    @Column(name = "other_deductions", precision = 12, scale = 2)
    private BigDecimal otherDeductions = BigDecimal.ZERO;

    @Column(name = "net_salary", precision = 12, scale = 2)
    private BigDecimal netSalary = BigDecimal.ZERO;

    @Column(name = "cl_balance")
    private Integer clBalance = 12;

    @Column(name = "el_balance")
    private Integer elBalance = 10;
}
