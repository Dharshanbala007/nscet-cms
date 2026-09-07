package com.nscet.cms.core.service;

import com.nscet.cms.db.entity.payroll.*;
import com.nscet.cms.db.repository.payroll.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class PayrollService {

    private final LeaveMasterRepository leaveRepo;
    private final StaffSalaryRepository staffSalaryRepo;
    private final AttendanceRecordRepository attendanceRepo;
    private final SalaryIncrementRepository incrementRepo;
    private final MonthlyPayrollRunRepository monthlyRunRepo;
    private final LatePermissionRepository latePermissionRepo;

    public PayrollService(LeaveMasterRepository leaveRepo,
                          StaffSalaryRepository staffSalaryRepo,
                          AttendanceRecordRepository attendanceRepo,
                          SalaryIncrementRepository incrementRepo,
                          MonthlyPayrollRunRepository monthlyRunRepo,
                          LatePermissionRepository latePermissionRepo) {
        this.leaveRepo = leaveRepo;
        this.staffSalaryRepo = staffSalaryRepo;
        this.attendanceRepo = attendanceRepo;
        this.incrementRepo = incrementRepo;
        this.monthlyRunRepo = monthlyRunRepo;
        this.latePermissionRepo = latePermissionRepo;
    }

    // Leave Master
    public List<LeaveMaster> getAllLeaves() { return leaveRepo.findAllActive(); }

    @Transactional
    public LeaveMaster saveLeave(LeaveMaster leave) {
        if (leave.getIsActive() == null) leave.setIsActive(true);
        return leaveRepo.save(leave);
    }

    @Transactional
    public void deleteLeave(Long id) {
        if (id != null) leaveRepo.deleteById(id);
    }

    // Staff Salary Master
    public List<StaffSalary> getAllStaffSalaries() { return staffSalaryRepo.findAllActive(); }

    public List<StaffSalary> searchStaffSalaries(String query) {
        if (query == null || query.trim().isEmpty()) return getAllStaffSalaries();
        return staffSalaryRepo.search(query.trim());
    }

    public Optional<StaffSalary> getStaffSalaryByCode(String code) {
        return staffSalaryRepo.findByStaffCode(code);
    }

    @Transactional
    public StaffSalary saveStaffSalary(StaffSalary staff) {
        if (staff.getBasicPay() == null) staff.setBasicPay(BigDecimal.ZERO);
        if (staff.getSpecialAllowance() == null) staff.setSpecialAllowance(BigDecimal.ZERO);
        if (staff.getHra() == null) staff.setHra(BigDecimal.ZERO);
        if (staff.getTaAmount() == null) staff.setTaAmount(BigDecimal.ZERO);
        if (staff.getWashingAllowance() == null) staff.setWashingAllowance(BigDecimal.ZERO);
        if (staff.getConveyance() == null) staff.setConveyance(BigDecimal.ZERO);

        BigDecimal gross = staff.getBasicPay()
                .add(staff.getSpecialAllowance())
                .add(staff.getHra())
                .add(staff.getTaAmount())
                .add(staff.getWashingAllowance())
                .add(staff.getConveyance());
        staff.setGrossSalary(gross);

        if (staff.getEpfDeduction() == null) staff.setEpfDeduction(BigDecimal.ZERO);
        if (staff.getEsiDeduction() == null) staff.setEsiDeduction(BigDecimal.ZERO);
        if (staff.getIncomeTax() == null) staff.setIncomeTax(BigDecimal.ZERO);
        if (staff.getProfessionalTax() == null) staff.setProfessionalTax(BigDecimal.ZERO);
        if (staff.getStaffClub() == null) staff.setStaffClub(BigDecimal.ZERO);
        if (staff.getOtherDeductions() == null) staff.setOtherDeductions(BigDecimal.ZERO);

        BigDecimal deductions = staff.getEpfDeduction()
                .add(staff.getEsiDeduction())
                .add(staff.getIncomeTax())
                .add(staff.getProfessionalTax())
                .add(staff.getStaffClub())
                .add(staff.getOtherDeductions());
        staff.setNetSalary(gross.subtract(deductions));

        if (staff.getIsActive() == null) staff.setIsActive(true);
        return staffSalaryRepo.save(staff);
    }

    // Attendance
    public List<AttendanceRecord> getAttendanceByDate(LocalDate date) {
        return attendanceRepo.findByAttendanceDate(date);
    }

    public List<AttendanceRecord> getAttendanceBetween(LocalDate start, LocalDate end) {
        LocalDate from = start != null ? start : LocalDate.of(1970, 1, 1);
        LocalDate to = end != null ? end : LocalDate.of(2099, 12, 31);
        if (from.isAfter(to)) { LocalDate t = from; from = to; to = t; }
        return attendanceRepo.findByDateRange(from, to);
    }

    @Transactional
    public AttendanceRecord saveAttendance(AttendanceRecord rec) {
        if (rec.getIsActive() == null) rec.setIsActive(true);
        return attendanceRepo.save(rec);
    }

    // Salary Increments
    public List<SalaryIncrement> getAllIncrements() { return incrementRepo.findAllActive(); }

    @Transactional
    public SalaryIncrement applyIncrement(SalaryIncrement inc) {
        if (inc.getIsActive() == null) inc.setIsActive(true);
        SalaryIncrement saved = incrementRepo.save(inc);

        // Update StaffSalary master
        staffSalaryRepo.findByStaffCode(inc.getStaffCode()).ifPresent(staff -> {
            staff.setBasicPay(inc.getNewBasic());
            staff.setSpecialAllowance(inc.getNewSpecialAllowance());
            saveStaffSalary(staff);
        });
        return saved;
    }

    @Transactional
    public List<SalaryIncrement> applyBulkIncrements(List<SalaryIncrement> increments) {
        if (increments == null || increments.isEmpty()) return List.of();
        List<SalaryIncrement> savedList = new java.util.ArrayList<>();
        for (SalaryIncrement inc : increments) {
            savedList.add(applyIncrement(inc));
        }
        return savedList;
    }

    // Monthly Payroll Calculation Run
    public List<MonthlyPayrollRun> getMonthlyRun(String payPeriod) {
        return monthlyRunRepo.findByPayPeriod(payPeriod);
    }

    public List<MonthlyPayrollRun> getAllMonthlyRuns() {
        return monthlyRunRepo.findAllActive();
    }

    @Transactional
    public List<MonthlyPayrollRun> calculateMonthlyRun(String payPeriod, int workingDays) {
        List<StaffSalary> allStaff = staffSalaryRepo.findAllActive();
        List<MonthlyPayrollRun> runs = new java.util.ArrayList<>();
        for (StaffSalary s : allStaff) {
            MonthlyPayrollRun run = new MonthlyPayrollRun();
            run.setPayPeriod(payPeriod);
            run.setStaffCode(s.getStaffCode());
            run.setStaffName(s.getStaffName());
            run.setDepartment(s.getDepartment());
            run.setWorkingDays(workingDays);
            run.setPaidDays(workingDays);
            run.setLopDays(0);

            run.setBasicPay(s.getBasicPay());
            run.setSpecialAllowance(s.getSpecialAllowance());
            run.setHra(s.getHra());
            run.setConveyance(s.getConveyance());
            run.setWashingAllowance(s.getWashingAllowance());
            run.setGrossPay(s.getGrossSalary());

            run.setLopDeduction(BigDecimal.ZERO);
            run.setEpfDeduction(s.getEpfDeduction());
            run.setEsiDeduction(s.getEsiDeduction());
            run.setIncomeTax(s.getIncomeTax());
            run.setProfessionalTax(s.getProfessionalTax());
            run.setStaffClub(s.getStaffClub());

            BigDecimal totalDeductions = (s.getEpfDeduction() != null ? s.getEpfDeduction() : BigDecimal.ZERO)
                    .add(s.getEsiDeduction() != null ? s.getEsiDeduction() : BigDecimal.ZERO)
                    .add(s.getIncomeTax() != null ? s.getIncomeTax() : BigDecimal.ZERO)
                    .add(s.getProfessionalTax() != null ? s.getProfessionalTax() : BigDecimal.ZERO)
                    .add(s.getStaffClub() != null ? s.getStaffClub() : BigDecimal.ZERO);
            run.setTotalDeductions(totalDeductions);
            BigDecimal gross = s.getGrossSalary() != null ? s.getGrossSalary() : BigDecimal.ZERO;
            run.setNetPay(gross.subtract(totalDeductions));
            run.setIsActive(true);
            runs.add(run);
        }
        return monthlyRunRepo.saveAll(runs);
    }

    @Transactional
    public List<MonthlyPayrollRun> saveMonthlyPayrollRuns(List<MonthlyPayrollRun> runs) {
        if (runs == null || runs.isEmpty()) return List.of();
        for (MonthlyPayrollRun r : runs) {
            if (r.getIsActive() == null) r.setIsActive(true);
        }
        return monthlyRunRepo.saveAll(runs);
    }

    // Late / Permission Details
    public List<LatePermission> getAllLatePermissions() {
        return latePermissionRepo.findAllActive();
    }

    public List<LatePermission> getLatePermissionsByDateRange(LocalDate start, LocalDate end) {
        return latePermissionRepo.findByDateRange(start, end);
    }

    @Transactional
    public LatePermission saveLatePermission(LatePermission lp) {
        if (lp.getIsActive() == null) lp.setIsActive(true);
        return latePermissionRepo.save(lp);
    }

    @Transactional
    public void deleteLatePermission(Long id) {
        if (id != null) {
            latePermissionRepo.deleteById(id);
        }
    }
}
