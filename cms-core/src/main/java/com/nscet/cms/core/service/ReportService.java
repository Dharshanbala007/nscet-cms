package com.nscet.cms.core.service;

import com.nscet.cms.db.entity.*;
import com.nscet.cms.db.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ReportService {

    private final FeeReceiptRepository feeReceiptRepository;
    private final StudentMasterRepository studentMasterRepository;
    private final StudentDetailsRepository studentDetailsRepository;
    private final FeesDetailsRepository feesDetailsRepository;
    private final BankMasterRepository bankMasterRepository;
    private final DepartmentMasterRepository departmentMasterRepository;
    private final FeesMasterRepository feesMasterRepository;

    public ReportService(FeeReceiptRepository feeReceiptRepository,
                         StudentMasterRepository studentMasterRepository,
                         StudentDetailsRepository studentDetailsRepository,
                         FeesDetailsRepository feesDetailsRepository,
                         BankMasterRepository bankMasterRepository,
                         DepartmentMasterRepository departmentMasterRepository,
                         FeesMasterRepository feesMasterRepository) {
        this.feeReceiptRepository = feeReceiptRepository;
        this.studentMasterRepository = studentMasterRepository;
        this.studentDetailsRepository = studentDetailsRepository;
        this.feesDetailsRepository = feesDetailsRepository;
        this.bankMasterRepository = bankMasterRepository;
        this.departmentMasterRepository = departmentMasterRepository;
        this.feesMasterRepository = feesMasterRepository;
    }

    // 1. Student Receipt Details
    public List<StudentReceiptDetailsDto> getStudentReceiptDetails(Long deptId, String search) {
        List<FeeReceipt> receipts = feeReceiptRepository.findAll();
        List<StudentReceiptDetailsDto> results = new ArrayList<>();

        for (FeeReceipt fr : receipts) {
            StudentMaster s = fr.getStudent();
            if (s == null) continue;

            if (search != null && !search.trim().isEmpty()) {
                String q = search.trim().toLowerCase();
                boolean matches = s.getRollNumber().toLowerCase().contains(q)
                        || s.getName().toLowerCase().contains(q)
                        || (s.getRegistrationNo() != null && s.getRegistrationNo().toLowerCase().contains(q));
                if (!matches) continue;
            }

            if (fr.getItems() == null || fr.getItems().isEmpty()) {
                StudentReceiptDetailsDto dto = new StudentReceiptDetailsDto();
                dto.setReceiptNo(fr.getReceiptNumber());
                dto.setReceiptDate(fr.getReceiptDate());
                dto.setRollNo(s.getRollNumber());
                dto.setStudentName(s.getName());
                dto.setRegNo(s.getRegistrationNo());
                dto.setDept("CSE");
                dto.setFeeName("Tuition Fee");
                dto.setAmount(fr.getTotalAmount());
                dto.setSemester(3);
                dto.setRemarks(fr.getPaymentMode());
                results.add(dto);
            } else {
                for (FeeReceiptItem item : fr.getItems()) {
                    StudentReceiptDetailsDto dto = new StudentReceiptDetailsDto();
                    dto.setReceiptNo(fr.getReceiptNumber());
                    dto.setReceiptDate(fr.getReceiptDate());
                    dto.setRollNo(s.getRollNumber());
                    dto.setStudentName(s.getName());
                    dto.setRegNo(s.getRegistrationNo());
                    dto.setDept("CSE");
                    dto.setFeeName(item.getFeesName() != null ? item.getFeesName().getName() : "College Fee");
                    dto.setAmount(item.getAmount());
                    dto.setSemester(3);
                    dto.setRemarks(fr.getPaymentMode());
                    results.add(dto);
                }
            }
        }
        return results;
    }

    // 2. Pending Fees
    public List<PendingFeesDto> getPendingFees(String academicYear, Integer semester, String deptCode) {
        List<StudentMaster> students = studentMasterRepository.findAll();
        List<PendingFeesDto> list = new ArrayList<>();

        for (StudentMaster s : students) {
            PendingFeesDto dto = new PendingFeesDto();
            dto.setDept(s.getRollNumber().contains("CSE") ? "CSE" : (s.getRollNumber().contains("ECE") ? "ECE" : "MECH"));
            dto.setRollNo(s.getRollNumber());
            dto.setStudentName(s.getName());
            dto.setQuota(s.getAdmissionType() != null ? s.getAdmissionType() : "Government");
            dto.setAdmissionType("Fresh");
            dto.setCommunity(s.getCommunity() != null ? s.getCommunity() : "BC");
            dto.setPreviousPending(BigDecimal.ZERO);

            BigDecimal tuition = new BigDecimal("45000");
            BigDecimal other = new BigDecimal("14800");
            BigDecimal total = tuition.add(other);
            BigDecimal paid = s.getRollNumber().endsWith("1") || s.getRollNumber().endsWith("3") ? total : new BigDecimal("25000");
            BigDecimal balance = total.subtract(paid);

            dto.setTuitionFees(tuition);
            dto.setOtherFees(other);
            dto.setScholarship(BigDecimal.ZERO);
            dto.setAnnaUnivReg(new BigDecimal("1500"));
            dto.setTotal(total);
            dto.setPaidAmount(paid);
            dto.setBalanceAmount(balance);
            dto.setFineAmount(BigDecimal.ZERO);
            list.add(dto);
        }
        return list;
    }

    // 3. Pending Bus Fees
    public List<PendingBusFeesDto> getPendingBusFees(String academicYear, String term, String deptCode) {
        List<StudentMaster> students = studentMasterRepository.findAll().stream()
                .filter(s -> "College Bus".equalsIgnoreCase(s.getTransportType()) || s.getBusStop() != null)
                .collect(Collectors.toList());

        List<PendingBusFeesDto> list = new ArrayList<>();
        for (StudentMaster s : students) {
            PendingBusFeesDto dto = new PendingBusFeesDto();
            dto.setDept(s.getRollNumber().contains("CSE") ? "CSE" : "ECE");
            dto.setRollNo(s.getRollNumber());
            dto.setStudentName(s.getName());
            dto.setQuota(s.getAdmissionType() != null ? s.getAdmissionType() : "Government");
            dto.setBusStopName(s.getBusStop() != null ? s.getBusStop() : "Theni Bus Stand");
            dto.setPreviousPending(BigDecimal.ZERO);
            dto.setBusFee(new BigDecimal("12000"));
            dto.setPaidAmount(new BigDecimal("8000"));
            dto.setBalanceAmount(new BigDecimal("4000"));
            dto.setToBePaid(new BigDecimal("4000"));
            dto.setReceiptNo("BUS-2025-091");
            list.add(dto);
        }
        return list;
    }

    // 4. Application Report
    public List<ApplicationReportDto> getApplicationReport(LocalDate from, LocalDate to) {
        List<StudentMaster> students = studentMasterRepository.findAll();
        List<ApplicationReportDto> list = new ArrayList<>();
        int count = 1;

        for (StudentMaster s : students) {
            ApplicationReportDto dto = new ApplicationReportDto();
            dto.setSlNo(count++);
            dto.setAppNo("FY" + String.format("%03d", count));
            dto.setStudentName(s.getName());
            dto.setAddress(s.getAddress() != null ? s.getAddress() : s.getCity());
            dto.setHscMark("480");
            dto.setDoteCutOff("165.5");
            dto.setCommunity(s.getCommunity() != null ? s.getCommunity() : "BC");
            dto.setMgGq(s.getAdmissionType() != null ? s.getAdmissionType() : "Counseling");
            dto.setAmountPaid(new BigDecimal("5000"));
            dto.setDept(s.getRollNumber().contains("CSE") ? "CSE" : "ECE");
            dto.setSchoolName("GHSS Theni");
            dto.setHostel(s.getHostel() != null ? s.getHostel() : "No");
            list.add(dto);
        }
        return list;
    }

    // 5. Fees Paid & Pending Details Report
    public List<FeesDetailsReportDto> getFeesDetailsReport(String academicPeriod, String branch) {
        List<FeesDetailsReportDto> list = new ArrayList<>();
        String[] depts = {"CSE", "ECE", "MECH", "CE", "EEE", "IT", "AI"};

        for (String d : depts) {
            FeesDetailsReportDto dto = new FeesDetailsReportDto();
            dto.setBranch(d);
            dto.setSemester(3);
            dto.setStrength(40);
            dto.setPrePending(BigDecimal.ZERO);
            dto.setTuitionFee(new BigDecimal("1800000"));
            dto.setOtherFees(new BigDecimal("592000"));
            dto.setBusFees(new BigDecimal("480000"));
            dto.setTotalAmount(new BigDecimal("2872000"));
            dto.setPaidAmount(new BigDecimal("2500000"));
            dto.setPendingAmount(new BigDecimal("372000"));
            dto.setColAmt(new BigDecimal("1800000"));
            dto.setKarAmt(new BigDecimal("500000"));
            dto.setBusAmt(new BigDecimal("400000"));
            dto.setPendTuition(new BigDecimal("100000"));
            dto.setPendOther(new BigDecimal("92000"));
            dto.setPendBus(new BigDecimal("180000"));
            list.add(dto);
        }
        return list;
    }

    // 6. Exam Fees Overall Report
    public List<ExamFeesReportDto> getExamFeesReport(String dept, Integer semester) {
        List<StudentMaster> students = studentMasterRepository.findAll();
        List<ExamFeesReportDto> list = new ArrayList<>();

        for (StudentMaster s : students) {
            ExamFeesReportDto dto = new ExamFeesReportDto();
            dto.setDept(s.getRollNumber().contains("CSE") ? "CSE" : "ECE");
            dto.setRollNo(s.getRollNumber());
            dto.setStudentName(s.getName());
            dto.setExamName("Anna University End Sem Nov/Dec 2025");
            dto.setAmount(new BigDecimal("1800"));
            dto.setPaidAmount(new BigDecimal("1800"));
            dto.setBalanceAmount(BigDecimal.ZERO);
            dto.setStatus("PAID");
            list.add(dto);
        }
        return list;
    }

    // 7. Receipt Bank Checking
    public List<ReceiptBankCheckingDto> getReceiptBankChecking(LocalDate from, LocalDate to, String accountNo) {
        List<FeeReceipt> receipts = feeReceiptRepository.findAll();
        List<ReceiptBankCheckingDto> list = new ArrayList<>();

        for (FeeReceipt fr : receipts) {
            ReceiptBankCheckingDto dto = new ReceiptBankCheckingDto();
            dto.setReceiptNo(fr.getReceiptNumber());
            dto.setReceiptDate(fr.getReceiptDate());
            dto.setBankName(fr.getBaseAccount() != null ? fr.getBaseAccount() : "TMB Main");
            dto.setAccountNo(fr.getBank() != null ? fr.getBank().getAccountNumber() : "0071001404500");
            dto.setPaymentMode(fr.getPaymentMode() != null ? fr.getPaymentMode() : "CASH");
            dto.setAmount(fr.getTotalAmount());
            dto.setStatus(fr.getStatus() != null ? fr.getStatus() : "VERIFIED");
            list.add(dto);
        }
        return list;
    }

    // 8. Headwise Details
    public List<HeadwiseDetailsDto> getHeadwiseDetails(LocalDate from, LocalDate to, String type) {
        List<FeeReceipt> receipts = feeReceiptRepository.findAll();
        List<HeadwiseDetailsDto> list = new ArrayList<>();

        for (FeeReceipt fr : receipts) {
            if (fr.getItems() == null || fr.getItems().isEmpty()) {
                HeadwiseDetailsDto dto = new HeadwiseDetailsDto();
                dto.setReceiptNo(fr.getReceiptNumber());
                dto.setReceiptDate(fr.getReceiptDate());
                dto.setDept("CSE");
                dto.setRollNo(fr.getStudent() != null ? fr.getStudent().getRollNumber() : "23CSE001");
                dto.setStudentName(fr.getStudent() != null ? fr.getStudent().getName() : "Arun Kumar S");
                dto.setFeeHead("Tuition Fee");
                dto.setAmount(fr.getTotalAmount());
                list.add(dto);
            } else {
                for (FeeReceiptItem item : fr.getItems()) {
                    HeadwiseDetailsDto dto = new HeadwiseDetailsDto();
                    dto.setReceiptNo(fr.getReceiptNumber());
                    dto.setReceiptDate(fr.getReceiptDate());
                    dto.setDept("CSE");
                    dto.setRollNo(fr.getStudent() != null ? fr.getStudent().getRollNumber() : "23CSE001");
                    dto.setStudentName(fr.getStudent() != null ? fr.getStudent().getName() : "Arun Kumar S");
                    dto.setFeeHead(item.getFeesName() != null ? item.getFeesName().getName() : "Tuition Fee");
                    dto.setAmount(item.getAmount());
                    list.add(dto);
                }
            }
        }
        return list;
    }

    // 9. Strength Report
    public List<StrengthReportDto> getStrengthReport(String dept) {
        List<StrengthReportDto> list = new ArrayList<>();
        String[] depts = {"CSE", "ECE", "MECH", "CE", "EEE", "IT", "AI"};

        for (String d : depts) {
            StrengthReportDto dto = new StrengthReportDto();
            dto.setDepartment(d);
            dto.setDegree("B.E.");
            dto.setYear("II Year");
            dto.setSemester(3);
            dto.setMaleCount(22);
            dto.setFemaleCount(18);
            dto.setTotalCount(40);
            dto.setOcCount(10);
            dto.setBcCount(18);
            dto.setMbcCount(8);
            dto.setScstCount(4);
            list.add(dto);
        }
        return list;
    }

    // 10. DFCR Report
    public List<DfcrReportDto> getDfcrReport(LocalDate from, LocalDate to) {
        List<FeeReceipt> receipts = feeReceiptRepository.findAll();
        List<DfcrReportDto> list = new ArrayList<>();

        for (FeeReceipt fr : receipts) {
            DfcrReportDto dto = new DfcrReportDto();
            dto.setReceiptNo(fr.getReceiptNumber());
            dto.setReceiptDate(fr.getReceiptDate());
            dto.setRollNo(fr.getStudent() != null ? fr.getStudent().getRollNumber() : "23CSE001");
            dto.setStudentName(fr.getStudent() != null ? fr.getStudent().getName() : "Arun Kumar S");
            dto.setDept("CSE");
            dto.setBaseAccount(fr.getBaseAccount() != null ? fr.getBaseAccount() : "TMB Main");
            dto.setPaymentMode(fr.getPaymentMode() != null ? fr.getPaymentMode() : "CASH");
            dto.setTotalAmount(fr.getTotalAmount());
            list.add(dto);
        }
        return list;
    }

    // 11. DFCR Groupwise Report
    public List<DfcrGroupwiseDto> getDfcrGroupwiseReport(LocalDate from, LocalDate to) {
        List<DfcrGroupwiseDto> list = new ArrayList<>();
        list.add(new DfcrGroupwiseDto("College Fees", new BigDecimal("1250000.00"), 25));
        list.add(new DfcrGroupwiseDto("Exam Fees", new BigDecimal("180000.00"), 30));
        list.add(new DfcrGroupwiseDto("Bus Fees", new BigDecimal("340000.00"), 20));
        list.add(new DfcrGroupwiseDto("Hostel Fees", new BigDecimal("220000.00"), 15));
        list.add(new DfcrGroupwiseDto("Miscellaneous", new BigDecimal("45000.00"), 10));
        return list;
    }

    // Data DTO Classes
    public static class StudentReceiptDetailsDto {
        private String receiptNo;
        private LocalDate receiptDate;
        private String rollNo;
        private String studentName;
        private String regNo;
        private String dept;
        private String feeName;
        private BigDecimal amount;
        private Integer semester;
        private String remarks;

        public String getReceiptNo() { return receiptNo; }
        public void setReceiptNo(String receiptNo) { this.receiptNo = receiptNo; }
        public LocalDate getReceiptDate() { return receiptDate; }
        public void setReceiptDate(LocalDate receiptDate) { this.receiptDate = receiptDate; }
        public String getRollNo() { return rollNo; }
        public void setRollNo(String rollNo) { this.rollNo = rollNo; }
        public String getStudentName() { return studentName; }
        public void setStudentName(String studentName) { this.studentName = studentName; }
        public String getRegNo() { return regNo; }
        public void setRegNo(String regNo) { this.regNo = regNo; }
        public String getDept() { return dept; }
        public void setDept(String dept) { this.dept = dept; }
        public String getFeeName() { return feeName; }
        public void setFeeName(String feeName) { this.feeName = feeName; }
        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
        public Integer getSemester() { return semester; }
        public void setSemester(Integer semester) { this.semester = semester; }
        public String getRemarks() { return remarks; }
        public void setRemarks(String remarks) { this.remarks = remarks; }
    }

    public static class PendingFeesDto {
        private String dept;
        private String rollNo;
        private String studentName;
        private String quota;
        private String admissionType;
        private String community;
        private BigDecimal previousPending;
        private BigDecimal tuitionFees;
        private BigDecimal otherFees;
        private BigDecimal scholarship;
        private BigDecimal annaUnivReg;
        private BigDecimal total;
        private BigDecimal paidAmount;
        private BigDecimal balanceAmount;
        private BigDecimal fineAmount;

        public String getDept() { return dept; }
        public void setDept(String dept) { this.dept = dept; }
        public String getRollNo() { return rollNo; }
        public void setRollNo(String rollNo) { this.rollNo = rollNo; }
        public String getStudentName() { return studentName; }
        public void setStudentName(String studentName) { this.studentName = studentName; }
        public String getQuota() { return quota; }
        public void setQuota(String quota) { this.quota = quota; }
        public String getAdmissionType() { return admissionType; }
        public void setAdmissionType(String admissionType) { this.admissionType = admissionType; }
        public String getCommunity() { return community; }
        public void setCommunity(String community) { this.community = community; }
        public BigDecimal getPreviousPending() { return previousPending; }
        public void setPreviousPending(BigDecimal previousPending) { this.previousPending = previousPending; }
        public BigDecimal getTuitionFees() { return tuitionFees; }
        public void setTuitionFees(BigDecimal tuitionFees) { this.tuitionFees = tuitionFees; }
        public BigDecimal getOtherFees() { return otherFees; }
        public void setOtherFees(BigDecimal otherFees) { this.otherFees = otherFees; }
        public BigDecimal getScholarship() { return scholarship; }
        public void setScholarship(BigDecimal scholarship) { this.scholarship = scholarship; }
        public BigDecimal getAnnaUnivReg() { return annaUnivReg; }
        public void setAnnaUnivReg(BigDecimal annaUnivReg) { this.annaUnivReg = annaUnivReg; }
        public BigDecimal getTotal() { return total; }
        public void setTotal(BigDecimal total) { this.total = total; }
        public BigDecimal getPaidAmount() { return paidAmount; }
        public void setPaidAmount(BigDecimal paidAmount) { this.paidAmount = paidAmount; }
        public BigDecimal getBalanceAmount() { return balanceAmount; }
        public void setBalanceAmount(BigDecimal balanceAmount) { this.balanceAmount = balanceAmount; }
        public BigDecimal getFineAmount() { return fineAmount; }
        public void setFineAmount(BigDecimal fineAmount) { this.fineAmount = fineAmount; }
    }

    public static class PendingBusFeesDto {
        private String dept;
        private String rollNo;
        private String studentName;
        private String quota;
        private String busStopName;
        private BigDecimal previousPending;
        private BigDecimal busFee;
        private BigDecimal paidAmount;
        private BigDecimal balanceAmount;
        private BigDecimal toBePaid;
        private String receiptNo;

        public String getDept() { return dept; }
        public void setDept(String dept) { this.dept = dept; }
        public String getRollNo() { return rollNo; }
        public void setRollNo(String rollNo) { this.rollNo = rollNo; }
        public String getStudentName() { return studentName; }
        public void setStudentName(String studentName) { this.studentName = studentName; }
        public String getQuota() { return quota; }
        public void setQuota(String quota) { this.quota = quota; }
        public String getBusStopName() { return busStopName; }
        public void setBusStopName(String busStopName) { this.busStopName = busStopName; }
        public BigDecimal getPreviousPending() { return previousPending; }
        public void setPreviousPending(BigDecimal previousPending) { this.previousPending = previousPending; }
        public BigDecimal getBusFee() { return busFee; }
        public void setBusFee(BigDecimal busFee) { this.busFee = busFee; }
        public BigDecimal getPaidAmount() { return paidAmount; }
        public void setPaidAmount(BigDecimal paidAmount) { this.paidAmount = paidAmount; }
        public BigDecimal getBalanceAmount() { return balanceAmount; }
        public void setBalanceAmount(BigDecimal balanceAmount) { this.balanceAmount = balanceAmount; }
        public BigDecimal getToBePaid() { return toBePaid; }
        public void setToBePaid(BigDecimal toBePaid) { this.toBePaid = toBePaid; }
        public String getReceiptNo() { return receiptNo; }
        public void setReceiptNo(String receiptNo) { this.receiptNo = receiptNo; }
    }

    public static class ApplicationReportDto {
        private Integer slNo;
        private String appNo;
        private String studentName;
        private String address;
        private String hscMark;
        private String doteCutOff;
        private String community;
        private String mgGq;
        private BigDecimal amountPaid;
        private String dept;
        private String schoolName;
        private String hostel;

        public Integer getSlNo() { return slNo; }
        public void setSlNo(Integer slNo) { this.slNo = slNo; }
        public String getAppNo() { return appNo; }
        public void setAppNo(String appNo) { this.appNo = appNo; }
        public String getStudentName() { return studentName; }
        public void setStudentName(String studentName) { this.studentName = studentName; }
        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
        public String getHscMark() { return hscMark; }
        public void setHscMark(String hscMark) { this.hscMark = hscMark; }
        public String getDoteCutOff() { return doteCutOff; }
        public void setDoteCutOff(String doteCutOff) { this.doteCutOff = doteCutOff; }
        public String getCommunity() { return community; }
        public void setCommunity(String community) { this.community = community; }
        public String getMgGq() { return mgGq; }
        public void setMgGq(String mgGq) { this.mgGq = mgGq; }
        public BigDecimal getAmountPaid() { return amountPaid; }
        public void setAmountPaid(BigDecimal amountPaid) { this.amountPaid = amountPaid; }
        public String getDept() { return dept; }
        public void setDept(String dept) { this.dept = dept; }
        public String getSchoolName() { return schoolName; }
        public void setSchoolName(String schoolName) { this.schoolName = schoolName; }
        public String getHostel() { return hostel; }
        public void setHostel(String hostel) { this.hostel = hostel; }
    }

    public static class FeesDetailsReportDto {
        private String branch;
        private Integer semester;
        private Integer strength;
        private BigDecimal prePending;
        private BigDecimal tuitionFee;
        private BigDecimal otherFees;
        private BigDecimal busFees;
        private BigDecimal totalAmount;
        private BigDecimal paidAmount;
        private BigDecimal pendingAmount;
        private BigDecimal colAmt;
        private BigDecimal karAmt;
        private BigDecimal busAmt;
        private BigDecimal pendTuition;
        private BigDecimal pendOther;
        private BigDecimal pendBus;

        public String getBranch() { return branch; }
        public void setBranch(String branch) { this.branch = branch; }
        public Integer getSemester() { return semester; }
        public void setSemester(Integer semester) { this.semester = semester; }
        public Integer getStrength() { return strength; }
        public void setStrength(Integer strength) { this.strength = strength; }
        public BigDecimal getPrePending() { return prePending; }
        public void setPrePending(BigDecimal prePending) { this.prePending = prePending; }
        public BigDecimal getTuitionFee() { return tuitionFee; }
        public void setTuitionFee(BigDecimal tuitionFee) { this.tuitionFee = tuitionFee; }
        public BigDecimal getOtherFees() { return otherFees; }
        public void setOtherFees(BigDecimal otherFees) { this.otherFees = otherFees; }
        public BigDecimal getBusFees() { return busFees; }
        public void setBusFees(BigDecimal busFees) { this.busFees = busFees; }
        public BigDecimal getTotalAmount() { return totalAmount; }
        public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
        public BigDecimal getPaidAmount() { return paidAmount; }
        public void setPaidAmount(BigDecimal paidAmount) { this.paidAmount = paidAmount; }
        public BigDecimal getPendingAmount() { return pendingAmount; }
        public void setPendingAmount(BigDecimal pendingAmount) { this.pendingAmount = pendingAmount; }
        public BigDecimal getColAmt() { return colAmt; }
        public void setColAmt(BigDecimal colAmt) { this.colAmt = colAmt; }
        public BigDecimal getKarAmt() { return karAmt; }
        public void setKarAmt(BigDecimal karAmt) { this.karAmt = karAmt; }
        public BigDecimal getBusAmt() { return busAmt; }
        public void setBusAmt(BigDecimal busAmt) { this.busAmt = busAmt; }
        public BigDecimal getPendTuition() { return pendTuition; }
        public void setPendTuition(BigDecimal pendTuition) { this.pendTuition = pendTuition; }
        public BigDecimal getPendOther() { return pendOther; }
        public void setPendOther(BigDecimal pendOther) { this.pendOther = pendOther; }
        public BigDecimal getPendBus() { return pendBus; }
        public void setPendBus(BigDecimal pendBus) { this.pendBus = pendBus; }
    }

    public static class ExamFeesReportDto {
        private String dept;
        private String rollNo;
        private String studentName;
        private String examName;
        private BigDecimal amount;
        private BigDecimal paidAmount;
        private BigDecimal balanceAmount;
        private String status;

        public String getDept() { return dept; }
        public void setDept(String dept) { this.dept = dept; }
        public String getRollNo() { return rollNo; }
        public void setRollNo(String rollNo) { this.rollNo = rollNo; }
        public String getStudentName() { return studentName; }
        public void setStudentName(String studentName) { this.studentName = studentName; }
        public String getExamName() { return examName; }
        public void setExamName(String examName) { this.examName = examName; }
        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
        public BigDecimal getPaidAmount() { return paidAmount; }
        public void setPaidAmount(BigDecimal paidAmount) { this.paidAmount = paidAmount; }
        public BigDecimal getBalanceAmount() { return balanceAmount; }
        public void setBalanceAmount(BigDecimal balanceAmount) { this.balanceAmount = balanceAmount; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }

    public static class ReceiptBankCheckingDto {
        private String receiptNo;
        private LocalDate receiptDate;
        private String bankName;
        private String accountNo;
        private String paymentMode;
        private BigDecimal amount;
        private String status;

        public String getReceiptNo() { return receiptNo; }
        public void setReceiptNo(String receiptNo) { this.receiptNo = receiptNo; }
        public LocalDate getReceiptDate() { return receiptDate; }
        public void setReceiptDate(LocalDate receiptDate) { this.receiptDate = receiptDate; }
        public String getBankName() { return bankName; }
        public void setBankName(String bankName) { this.bankName = bankName; }
        public String getAccountNo() { return accountNo; }
        public void setAccountNo(String accountNo) { this.accountNo = accountNo; }
        public String getPaymentMode() { return paymentMode; }
        public void setPaymentMode(String paymentMode) { this.paymentMode = paymentMode; }
        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }

    public static class HeadwiseDetailsDto {
        private String receiptNo;
        private LocalDate receiptDate;
        private String dept;
        private String rollNo;
        private String studentName;
        private String feeHead;
        private BigDecimal amount;

        public String getReceiptNo() { return receiptNo; }
        public void setReceiptNo(String receiptNo) { this.receiptNo = receiptNo; }
        public LocalDate getReceiptDate() { return receiptDate; }
        public void setReceiptDate(LocalDate receiptDate) { this.receiptDate = receiptDate; }
        public String getDept() { return dept; }
        public void setDept(String dept) { this.dept = dept; }
        public String getRollNo() { return rollNo; }
        public void setRollNo(String rollNo) { this.rollNo = rollNo; }
        public String getStudentName() { return studentName; }
        public void setStudentName(String studentName) { this.studentName = studentName; }
        public String getFeeHead() { return feeHead; }
        public void setFeeHead(String feeHead) { this.feeHead = feeHead; }
        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
    }

    public static class StrengthReportDto {
        private String department;
        private String degree;
        private String year;
        private Integer semester;
        private Integer maleCount;
        private Integer femaleCount;
        private Integer totalCount;
        private Integer ocCount;
        private Integer bcCount;
        private Integer mbcCount;
        private Integer scstCount;

        public String getDepartment() { return department; }
        public void setDepartment(String department) { this.department = department; }
        public String getDegree() { return degree; }
        public void setDegree(String degree) { this.degree = degree; }
        public String getYear() { return year; }
        public void setYear(String year) { this.year = year; }
        public Integer getSemester() { return semester; }
        public void setSemester(Integer semester) { this.semester = semester; }
        public Integer getMaleCount() { return maleCount; }
        public void setMaleCount(Integer maleCount) { this.maleCount = maleCount; }
        public Integer getFemaleCount() { return femaleCount; }
        public void setFemaleCount(Integer femaleCount) { this.femaleCount = femaleCount; }
        public Integer getTotalCount() { return totalCount; }
        public void setTotalCount(Integer totalCount) { this.totalCount = totalCount; }
        public Integer getOcCount() { return ocCount; }
        public void setOcCount(Integer ocCount) { this.ocCount = ocCount; }
        public Integer getBcCount() { return bcCount; }
        public void setBcCount(Integer bcCount) { this.bcCount = bcCount; }
        public Integer getMbcCount() { return mbcCount; }
        public void setMbcCount(Integer mbcCount) { this.mbcCount = mbcCount; }
        public Integer getScstCount() { return scstCount; }
        public void setScstCount(Integer scstCount) { this.scstCount = scstCount; }
    }

    public static class DfcrReportDto {
        private String receiptNo;
        private LocalDate receiptDate;
        private String rollNo;
        private String studentName;
        private String dept;
        private String baseAccount;
        private String paymentMode;
        private BigDecimal totalAmount;

        public String getReceiptNo() { return receiptNo; }
        public void setReceiptNo(String receiptNo) { this.receiptNo = receiptNo; }
        public LocalDate getReceiptDate() { return receiptDate; }
        public void setReceiptDate(LocalDate receiptDate) { this.receiptDate = receiptDate; }
        public String getRollNo() { return rollNo; }
        public void setRollNo(String rollNo) { this.rollNo = rollNo; }
        public String getStudentName() { return studentName; }
        public void setStudentName(String studentName) { this.studentName = studentName; }
        public String getDept() { return dept; }
        public void setDept(String dept) { this.dept = dept; }
        public String getBaseAccount() { return baseAccount; }
        public void setBaseAccount(String baseAccount) { this.baseAccount = baseAccount; }
        public String getPaymentMode() { return paymentMode; }
        public void setPaymentMode(String paymentMode) { this.paymentMode = paymentMode; }
        public BigDecimal getTotalAmount() { return totalAmount; }
        public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    }

    public static class DfcrGroupwiseDto {
        private String feeGroup;
        private BigDecimal totalCollected;
        private Integer receiptCount;

        public DfcrGroupwiseDto(String feeGroup, BigDecimal totalCollected, Integer receiptCount) {
            this.feeGroup = feeGroup;
            this.totalCollected = totalCollected;
            this.receiptCount = receiptCount;
        }

        public String getFeeGroup() { return feeGroup; }
        public void setFeeGroup(String feeGroup) { this.feeGroup = feeGroup; }
        public BigDecimal getTotalCollected() { return totalCollected; }
        public void setTotalCollected(BigDecimal totalCollected) { this.totalCollected = totalCollected; }
        public Integer getReceiptCount() { return receiptCount; }
        public void setReceiptCount(Integer receiptCount) { this.receiptCount = receiptCount; }
    }
}
