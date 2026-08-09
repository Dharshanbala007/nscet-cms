package com.nscet.cms.db.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "admin_fees_details")
public class FeesDetails extends BaseEntity {

    @Column(name = "from_date")
    private LocalDate fromDate;

    @Column(name = "to_date")
    private LocalDate toDate;

    @Column(name = "degree", length = 50)
    private String degree;

    @Column(name = "semester")
    private Integer semester;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quota_id")
    private QuotaMaster quota;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private DepartmentMaster department;

    @Column(name = "dept_type", length = 50)
    private String deptType;

    @Column(name = "admission_type", length = 50)
    private String admissionType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fees_name_id")
    private FeesMaster feesName;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;
}
