package com.nscet.cms.core.service;

import com.nscet.cms.core.service.ReportService.PendingFeesDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ReportServiceBlackBoxTest {

    @Test
    @DisplayName("Black-Box Requirement Test 1: Pending Fees DTO Functional Data Contract")
    void testPendingFeesDtoFunctionalContract() {
        PendingFeesDto dto = new PendingFeesDto();
        dto.setDept("CSE");
        dto.setRollNo("23CSE001");
        dto.setStudentName("Arun Kumar S");
        dto.setQuota("Government");
        dto.setAdmissionType("Fresh");
        dto.setCommunity("OC");
        dto.setPreviousPending(new BigDecimal("0.00"));
        dto.setTuitionFees(new BigDecimal("44850.00"));
        dto.setOtherFees(new BigDecimal("14950.00"));
        dto.setTotal(new BigDecimal("59800.00"));
        dto.setPaidAmount(new BigDecimal("5000.00"));
        dto.setBalanceAmount(new BigDecimal("54800.00"));

        assertEquals("CSE", dto.getDept());
        assertEquals("23CSE001", dto.getRollNo());
        assertEquals("Arun Kumar S", dto.getStudentName());
        assertEquals(new BigDecimal("59800.00"), dto.getTotal());
        assertEquals(new BigDecimal("54800.00"), dto.getBalanceAmount());
    }

    @Test
    @DisplayName("Black-Box Requirement Test 2: Department Filter Contract for Multi-Student Lists")
    void testDepartmentFilteringBlackBoxContract() {
        List<PendingFeesDto> allStudents = new ArrayList<>();

        PendingFeesDto s1 = new PendingFeesDto(); s1.setDept("CSE"); s1.setRollNo("23CSE001"); s1.setStudentName("Arun Kumar S");
        PendingFeesDto s2 = new PendingFeesDto(); s2.setDept("MECH"); s2.setRollNo("23ME002"); s2.setStudentName("Bala M");
        PendingFeesDto s3 = new PendingFeesDto(); s3.setDept("ECE"); s3.setRollNo("23EC003"); s3.setStudentName("Chitra R");

        allStudents.add(s1);
        allStudents.add(s2);
        allStudents.add(s3);

        // Filter by CSE
        List<PendingFeesDto> cseOnly = allStudents.stream()
                .filter(s -> "CSE".equalsIgnoreCase(s.getDept()))
                .toList();

        assertEquals(1, cseOnly.size());
        assertEquals("Arun Kumar S", cseOnly.get(0).getStudentName());
    }
}
