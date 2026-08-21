package com.nscet.cms.db.repository.payroll;

import com.nscet.cms.db.entity.payroll.AttendanceRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecord, Long> {

    @Query("SELECT a FROM AttendanceRecord a WHERE a.isActive = true AND a.attendanceDate = :date ORDER BY a.staffCode ASC")
    List<AttendanceRecord> findByAttendanceDate(@Param("date") LocalDate date);

    @Query("SELECT a FROM AttendanceRecord a WHERE a.isActive = true AND a.staffCode = :staffCode ORDER BY a.attendanceDate DESC")
    List<AttendanceRecord> findByStaffCode(@Param("staffCode") String staffCode);

    @Query("SELECT a FROM AttendanceRecord a WHERE a.isActive = true AND a.attendanceDate >= :startDate AND a.attendanceDate <= :endDate ORDER BY a.attendanceDate DESC")
    List<AttendanceRecord> findByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
