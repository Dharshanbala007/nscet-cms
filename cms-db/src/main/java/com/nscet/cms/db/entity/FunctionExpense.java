package com.nscet.cms.db.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "admin_function_expenses")
public class FunctionExpense extends BaseEntity {

    @Column(name = "function_name", nullable = false, length = 150)
    private String functionName;

    @Column(name = "department", length = 100)
    private String department;

    @Column(name = "expense_date", nullable = false)
    private LocalDate expenseDate;

    @Column(name = "allocated_budget", precision = 12, scale = 2)
    private BigDecimal allocatedBudget = BigDecimal.ZERO;

    @Column(name = "total_expense", precision = 12, scale = 2)
    private BigDecimal totalExpense = BigDecimal.ZERO;

    @Column(name = "balance_amount", precision = 12, scale = 2)
    private BigDecimal balanceAmount = BigDecimal.ZERO;

    @Column(name = "status", length = 50)
    private String status = "Completed";

    @Column(name = "remarks", length = 500)
    private String remarks;
}
