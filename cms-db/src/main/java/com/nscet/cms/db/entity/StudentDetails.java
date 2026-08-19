package com.nscet.cms.db.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "admin_student_details",
       uniqueConstraints = @UniqueConstraint(columnNames = {"student_id", "semester", "admission_year"}))
public class StudentDetails extends BaseEntity {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id", nullable = false)
    private StudentMaster student;

    @Column(name = "semester", nullable = false)
    private Integer semester;

    @Column(name = "caste_category", length = 50)
    private String casteCategory;

    @Column(name = "bus_stop", length = 100)
    private String busStop;

    @Column(name = "hostel", length = 50)
    private String hostel;

    @Column(name = "transport_type", length = 50)
    private String transportType;

    @Column(name = "state", length = 50)
    private String state;

    @Column(name = "admission_year", nullable = false)
    private Integer admissionYear;

    @Column(name = "sem_type", length = 20)
    private String semType;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "department_id")
    private DepartmentMaster department;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "quota_id")
    private QuotaMaster quota;

    @Column(name = "degree", length = 50)
    private String degree;

    @Column(name = "section", length = 5)
    private String section;

    @Column(name = "academic_year", length = 20)
    private String academicYear;
}
