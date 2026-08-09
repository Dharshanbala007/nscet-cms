package com.nscet.cms.db.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "admin_day_settlements")
public class DaySettlement extends BaseEntity {

    @Column(name = "settlement_date", nullable = false)
    private LocalDate settlementDate;

    @Column(name = "opening_balance")
    private BigDecimal openingBalance;

    @Column(name = "cash_collection")
    private BigDecimal cashCollection;

    @Column(name = "bank_collection")
    private BigDecimal bankCollection;

    @Column(name = "online_collection")
    private BigDecimal onlineCollection;

    @Column(name = "closing_balance")
    private BigDecimal closingBalance;

    @Column(name = "total_receipts")
    private Integer totalReceipts;

    @Column(name = "status", length = 20)
    private String status = "PENDING";
}
