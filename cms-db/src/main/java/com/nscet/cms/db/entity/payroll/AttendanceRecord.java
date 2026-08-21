package com.nscet.cms.db.entity.payroll;

import com.nscet.cms.db.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "payroll_attendance")
public class AttendanceRecord extends BaseEntity {

    @Column(name = "attendance_date", nullable = false)
    private LocalDate attendanceDate;

    @Column(name = "staff_code", nullable = false, length = 30)
    private String staffCode;

    @Column(name = "staff_name", length = 100)
    private String staffName;

    @Column(name = "department", length = 100)
    private String department;

    @Column(name = "session_type", length = 20)
    private String sessionType = "FULL_DAY";

    @Column(name = "attendance_type", length = 20)
    private String attendanceType = "PRESENT";

    @Column(name = "remarks", length = 255)
    private String remarks;
}
