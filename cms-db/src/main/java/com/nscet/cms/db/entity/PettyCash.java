package com.nscet.cms.db.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "accounts_petty_cash")
public class PettyCash extends BaseEntity {

    @Column(name = "voucher_no", unique = true, nullable = false, length = 30)
    private String voucherNo;

    @Column(name = "voucher_date", nullable = false)
    private LocalDate voucherDate;

    @Column(name = "staff_name", length = 100)
    private String staffName;

    @Column(name = "department", length = 100)
    private String department;

    @Column(name = "amount", precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(name = "purpose", length = 500)
    private String purpose;

    @Column(name = "transaction_type", length = 20)
    private String transactionType;

    @Column(name = "remarks", length = 500)
    private String remarks;
}
