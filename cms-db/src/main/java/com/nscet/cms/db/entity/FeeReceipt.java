package com.nscet.cms.db.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "admin_fee_receipts")
public class FeeReceipt extends BaseEntity {

    @Column(name = "receipt_number", unique = true, nullable = false, length = 30)
    private String receiptNumber;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id", nullable = false)
    private StudentMaster student;

    @Column(name = "student_type", nullable = false, length = 20)
    private String studentType;

    @Column(name = "receipt_date", nullable = false)
    private LocalDate receiptDate;

    @Column(name = "academic_year", length = 20)
    private String academicYear;

    @Column(name = "payment_mode", length = 30)
    private String paymentMode;

    @Column(name = "base_account", length = 50)
    private String baseAccount;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "bank_id")
    private BankMaster bank;

    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;

    @Column(name = "status", length = 20)
    private String status = "ACTIVE";

    @Column(name = "pay_type", length = 30)
    private String payType;

    @Column(name = "dd_cheque_no", length = 30)
    private String ddChequeNo;

    @Column(name = "dd_cheque_bank", length = 100)
    private String ddChequeBank;

    @OneToMany(mappedBy = "receipt", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<FeeReceiptItem> items = new ArrayList<>();

    public void addItem(FeeReceiptItem item) {
        items.add(item);
        item.setReceipt(this);
    }
}
