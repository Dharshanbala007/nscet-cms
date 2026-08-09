package com.nscet.cms.db.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "admin_quota_master")
public class QuotaMaster extends BaseEntity {

    @Column(name = "code", unique = true, nullable = false, length = 10)
    private String code;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "percentage")
    private BigDecimal percentage;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "discount_amount")
    private BigDecimal discountAmount;

    @Column(name = "admission_type", length = 50)
    private String admissionType;
}
