package com.nscet.cms.db.entity.payroll;

import com.nscet.cms.db.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "payroll_late_permission")
public class LatePermission extends BaseEntity {

    @Column(name = "permission_date", nullable = false)
    private LocalDate permissionDate;

    @Column(name = "staff_code", nullable = false, length = 30)
    private String staffCode;

    @Column(name = "staff_name", nullable = false, length = 100)
    private String staffName;

    @Column(name = "department", length = 100)
    private String department;

    @Column(name = "permission_type", nullable = false, length = 50)
    private String permissionType;

    @Column(name = "duration_mins", length = 30)
    private String durationMins = "60 Mins";

    @Column(name = "reason", length = 255)
    private String reason;
}
