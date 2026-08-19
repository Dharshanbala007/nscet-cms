package com.nscet.cms.db.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "accounts_petty_voucher_items")
public class PettyVoucherItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voucher_id", nullable = false)
    private PettyVoucher pettyVoucher;

    @Column(name = "item_date")
    private LocalDate itemDate;

    @Column(name = "details", length = 200)
    private String details;

    @Column(name = "attendance_no", length = 30)
    private String attendanceNo;

    @Column(name = "item_type", length = 50)
    private String itemType;

    @Column(name = "amount", precision = 12, scale = 2)
    private BigDecimal amount;
}
