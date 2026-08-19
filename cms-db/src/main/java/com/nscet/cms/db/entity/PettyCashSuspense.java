package com.nscet.cms.db.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "accounts_petty_cash_suspense")
public class PettyCashSuspense extends BaseEntity {

    @Column(name = "voucher_no", unique = true, nullable = false, length = 30)
    private String voucherNo;

    @Column(name = "voucher_date", nullable = false)
    private LocalDate voucherDate;

    @Column(name = "college_or_hostel", length = 20)
    private String collegeOrHostel;

    @Column(name = "staff_name", length = 100)
    private String staffName;

    @Column(name = "department", length = 100)
    private String department;

    @Column(name = "designation", length = 100)
    private String designation;

    @Column(name = "amount", precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(name = "amount_in_words", length = 500)
    private String amountInWords;

    @Column(name = "purpose", length = 500)
    private String purpose;
}
