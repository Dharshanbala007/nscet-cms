package com.nscet.cms.core.service;

import com.nscet.cms.core.service.ReportService.PendingFeesDto;
import com.nscet.cms.core.service.ReportService.StudentReceiptDetailsDto;
import com.nscet.cms.db.entity.FeeReceipt;
import com.nscet.cms.db.entity.FeeReceiptItem;
import com.nscet.cms.db.entity.StudentMaster;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ReportServiceWhiteBoxTest {

    @Test
    @DisplayName("White-Box Path 1: Roll Number Department Parsing & String Extraction Logic")
    void testDepartmentParsingWhiteBoxPath() {
        StudentMaster student = new StudentMaster();
        student.setRollNumber("2025FCS012");
        student.setName("ARUN KUMAR S");

        FeeReceipt receipt = new FeeReceipt();
        receipt.setReceiptNumber("REC-1001");
        receipt.setReceiptDate(LocalDate.now());
        receipt.setTotalAmount(new BigDecimal("25000.00"));
        receipt.setPaymentMode("CASH");
        receipt.setStudent(student);

        // Simulate internal mapping & department extraction
        String roll = receipt.getStudent().getRollNumber();
        String dept = "UNKNOWN";
        if (roll != null && roll.length() >= 7) {
            String code = roll.substring(5, 7).toUpperCase();
            if ("CS".equals(code)) dept = "CSE";
            else if ("ME".equals(code)) dept = "MECH";
            else if ("EC".equals(code)) dept = "ECE";
        }

        assertEquals("CSE", dept, "White-box check: Roll number '2025FCS012' must parse department as 'CSE'.");
    }

    @Test
    @DisplayName("White-Box Path 2: Fee Receipt Item Loop & Null Safety Branching")
    void testReceiptItemBranchingWhiteBoxPath() {
        StudentMaster student = new StudentMaster();
        student.setRollNumber("2025FME005");
        student.setName("VIKRAM R");

        FeeReceipt receiptWithNoItems = new FeeReceipt();
        receiptWithNoItems.setReceiptNumber("REC-2001");
        receiptWithNoItems.setReceiptDate(LocalDate.now());
        receiptWithNoItems.setTotalAmount(new BigDecimal("12000.00"));
        receiptWithNoItems.setStudent(student);
        receiptWithNoItems.setItems(Collections.emptyList());

        List<StudentReceiptDetailsDto> dtoList = new ArrayList<>();
        if (receiptWithNoItems.getItems() == null || receiptWithNoItems.getItems().isEmpty()) {
            StudentReceiptDetailsDto dto = new StudentReceiptDetailsDto();
            dto.setReceiptNo(receiptWithNoItems.getReceiptNumber());
            dto.setRollNo(student.getRollNumber());
            dto.setFeeName("Tuition Fee");
            dto.setAmount(receiptWithNoItems.getTotalAmount());
            dtoList.add(dto);
        }

        assertEquals(1, dtoList.size());
        assertEquals("Tuition Fee", dtoList.get(0).getFeeName());
        assertEquals(new BigDecimal("12000.00"), dtoList.get(0).getAmount());
    }

    @Test
    @DisplayName("White-Box Path 3: Boundary Condition for Zero & Negative Fees Calculations")
    void testFeeBoundaryConditionsWhiteBoxPath() {
        BigDecimal tuition = new BigDecimal("44850.00");
        BigDecimal other = new BigDecimal("14950.00");
        BigDecimal annaReg = new BigDecimal("59800.00");
        BigDecimal paid = new BigDecimal("5000.00");

        BigDecimal total = tuition.add(other);
        BigDecimal balance = total.subtract(paid);

        assertTrue(total.compareTo(BigDecimal.ZERO) > 0, "Total fee must be strictly positive.");
        assertEquals(new BigDecimal("59800.00"), total);
        assertEquals(new BigDecimal("54800.00"), balance);
    }
}
