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
@Table(name = "accounts_petty_voucher")
public class PettyVoucher extends BaseEntity {

    @Column(name = "voucher_no", unique = true, nullable = false, length = 30)
    private String voucherNo;

    @Column(name = "voucher_date", nullable = false)
    private LocalDate voucherDate;

    @Column(name = "staff_name", length = 100)
    private String staffName;

    @Column(name = "staff_code", length = 30)
    private String staffCode;

    @Column(name = "designation", length = 100)
    private String designation;

    @Column(name = "department", length = 100)
    private String department;

    @Column(name = "suspense_voucher_no", length = 30)
    private String suspenseVoucherNo;

    @Column(name = "suspense_date")
    private LocalDate suspenseDate;

    @Column(name = "suspense_amount", precision = 12, scale = 2)
    private BigDecimal suspenseAmount;

    @Column(name = "purpose", length = 500)
    private String purpose;

    @Column(name = "total_amount", precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @OneToMany(mappedBy = "pettyVoucher", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PettyVoucherItem> items = new ArrayList<>();

    public void addItem(PettyVoucherItem item) {
        items.add(item);
        item.setPettyVoucher(this);
    }
}
