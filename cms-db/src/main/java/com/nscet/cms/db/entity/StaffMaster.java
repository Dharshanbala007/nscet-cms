package com.nscet.cms.db.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "admin_staff_master")
public class StaffMaster extends BaseEntity {

    @Column(name = "staff_code", unique = true, nullable = false, length = 20)
    private String staffCode;

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "address", length = 500)
    private String address;

    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "pin_code", length = 10)
    private String pinCode;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "category", length = 50)
    private String category;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "department_id")
    private DepartmentMaster department;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "designation_id")
    private DesignationMaster designation;

    @Column(name = "staff_group", length = 50)
    private String staffGroup;

    @Column(name = "college_code", length = 20)
    private String collegeCode;

    @Column(name = "transport", length = 10)
    private String transport;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "pf_active")
    private Boolean pfActive = false;

    @Column(name = "sex", length = 10)
    private String sex;

    @Column(name = "date_of_joining")
    private LocalDate dateOfJoining;

    @Column(name = "phone", length = 15)
    private String phone;

    @Column(name = "blood_group", length = 10)
    private String bloodGroup;

    @Column(name = "aadhar_number", length = 20)
    private String aadharNumber;

    @Column(name = "pan_number", length = 20)
    private String panNumber;

    @Column(name = "essl_id", length = 50)
    private String esslId;
}
