package com.nscet.cms.db.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "admin_bank_master")
public class BankMaster extends BaseEntity {

    @Column(name = "bank_short_name", nullable = false, length = 50)
    private String bankShortName;

    @Column(name = "account_number", nullable = false, length = 30)
    private String accountNumber;

    @Column(name = "bank_name", length = 100)
    private String bankName;

    @Column(name = "branch", length = 100)
    private String branch;

    @Column(name = "ifsc_code", length = 20)
    private String ifscCode;

    @Column(name = "remarks", length = 500)
    private String remarks;
}
