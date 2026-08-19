package com.nscet.cms.db.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "admin_fee_receipt_items")
public class FeeReceiptItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receipt_id", nullable = false)
    private FeeReceipt receipt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fees_name_id")
    private FeesMaster feesName;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "allocated_to", length = 30)
    private String allocatedTo;
}
