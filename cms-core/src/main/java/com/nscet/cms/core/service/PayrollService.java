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

    public PayrollService(LeaveMasterRepository leaveRepo,
                          StaffSalaryRepository staffSalaryRepo,
                          AttendanceRecordRepository attendanceRepo,
                          SalaryIncrementRepository incrementRepo,
                          MonthlyPayrollRunRepository monthlyRunRepo) {
        this.leaveRepo = leaveRepo;
        this.staffSalaryRepo = staffSalaryRepo;
        this.attendanceRepo = attendanceRepo;
        this.incrementRepo = incrementRepo;
        this.monthlyRunRepo = monthlyRunRepo;
    }

    // Leave Master
    public List<LeaveMaster> getAllLeaves() { return leaveRepo.findAllActive(); }

    @Transactional
    public LeaveMaster saveLeave(LeaveMaster leave) {
        if (leave.getIsActive() == null) leave.setIsActive(true);
        return leaveRepo.save(leave);
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

            BigDecimal totalDeductions = s.getEpfDeduction()
                    .add(s.getEsiDeduction())
                    .add(s.getIncomeTax())
                    .add(s.getProfessionalTax())
                    .add(s.getStaffClub());
            run.setTotalDeductions(totalDeductions);
            run.setNetPay(s.getGrossSalary().subtract(totalDeductions));
            run.setIsActive(true);

            monthlyRunRepo.save(run);
        }
        return getMonthlyRun(payPeriod);
    }
}
