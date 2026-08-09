package com.nscet.cms.db.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "admin_student_master")
public class StudentMaster extends BaseEntity {

    @Column(name = "roll_number", unique = true, nullable = false, length = 20)
    private String rollNumber;

    @Column(name = "registration_no", length = 20)
    private String registrationNo;

    @Column(name = "admission_no", unique = true, length = 20)
    private String admissionNo;

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "father_name", length = 150)
    private String fatherName;

    @Column(name = "phone", length = 15)
    private String phone;

    @Column(name = "gender", length = 10)
    private String gender;

    @Column(name = "aadhar_number", length = 20)
    private String aadharNumber;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "community", length = 50)
    private String community;

    @Column(name = "caste", length = 50)
    private String caste;

    @Column(name = "region", length = 100)
    private String region;

    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "address", length = 500)
    private String address;

    @Column(name = "blood_group", length = 10)
    private String bloodGroup;

    @Column(name = "medium", length = 20)
    private String medium;

    @Column(name = "mother_name", length = 150)
    private String motherName;

    @Column(name = "parent_phone", length = 15)
    private String parentPhone;

    @Column(name = "bus_stop", length = 100)
    private String busStop;

    @Column(name = "hostel", length = 50)
    private String hostel;

    @Column(name = "transport_type", length = 50)
    private String transportType;

    @Column(name = "state", length = 50)
    private String state;

    @Column(name = "admission_type", length = 50)
    private String admissionType;

    @Column(name = "date_of_joining")
    private LocalDate dateOfJoining;

    @Column(name = "section", length = 5)
    private String section;

    @Column(name = "occupation", length = 100)
    private String occupation;

    @Column(name = "religion", length = 50)
    private String religion;
}
