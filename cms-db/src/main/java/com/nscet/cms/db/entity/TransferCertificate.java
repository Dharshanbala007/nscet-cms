package com.nscet.cms.db.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "admin_transfer_certificates")
public class TransferCertificate extends BaseEntity {

    @Column(name = "tc_number", unique = true, nullable = false, length = 30)
    private String tcNumber;

    @Column(name = "serial_no", length = 30)
    private String serialNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private StudentMaster student;

    @Column(name = "academic_year", length = 20)
    private String academicYear;

    @Column(name = "admission_no", length = 20)
    private String admissionNo;

    @Column(name = "course", length = 50)
    private String course;

    @Column(name = "semester")
    private Integer semester;

    @Column(name = "tc_date")
    private LocalDate tcDate;

    @Column(name = "date_of_left")
    private LocalDate dateOfLeft;

    @Column(name = "character_conduct", length = 100)
    private String characterConduct;

    @Column(name = "tc_application_date")
    private LocalDate tcApplicationDate;

    @Column(name = "remarks", length = 500)
    private String remarks;

    @Column(name = "id_marks", length = 200)
    private String idMarks;

    @Column(name = "course_completion", length = 100)
    private String courseCompletion;

    @Column(name = "promotion_status", length = 100)
    private String promotionStatus;

    @Column(name = "fee_status", length = 100)
    private String feeStatus;

    @Column(name = "batch", length = 50)
    private String batch;

    @Column(name = "umis_no", length = 50)
    private String umisNo;
}
