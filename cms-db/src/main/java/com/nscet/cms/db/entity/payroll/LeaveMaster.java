package com.nscet.cms.db.entity.payroll;

import com.nscet.cms.db.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "payroll_leave_master")
public class LeaveMaster extends BaseEntity {

    @Column(name = "leave_code", nullable = false, unique = true, length = 20)
    private String leaveCode;

    @Column(name = "leave_name", nullable = false, length = 100)
    private String leaveName;

    @Column(name = "short_name", nullable = false, length = 20)
    private String shortName;

    @Column(name = "max_allowed")
    private Integer maxAllowed = 12;
}
