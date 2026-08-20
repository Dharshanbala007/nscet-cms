package com.nscet.cms.core.service;

import com.nscet.cms.db.entity.StudentMaster;
import com.nscet.cms.db.entity.TransferCertificate;
import com.nscet.cms.db.repository.StudentMasterRepository;
import com.nscet.cms.db.repository.TransferCertificateRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TransferCertificateService {

    private final TransferCertificateRepository tcRepository;
    private final StudentMasterRepository studentMasterRepository;

    public TransferCertificateService(TransferCertificateRepository tcRepository,
                                     StudentMasterRepository studentMasterRepository) {
        this.tcRepository = tcRepository;
        this.studentMasterRepository = studentMasterRepository;
    }

    @Transactional(readOnly = true)
    public Optional<TransferCertificate> findByTcNumber(String tcNumber) {
        return tcRepository.findByTcNumber(tcNumber);
    }

    @Transactional(readOnly = true)
    public Optional<TransferCertificate> findByStudent(StudentMaster student) {
        if (student == null) return Optional.empty();
        List<TransferCertificate> all = tcRepository.findAll();
        return all.stream()
                .filter(tc -> tc.getStudent() != null && tc.getStudent().getId().equals(student.getId()))
                .findFirst();
    }

    @Transactional
    public TransferCertificate saveTransferCertificate(TransferCertificate tc) {
        if (tc.getTcNumber() == null || tc.getTcNumber().trim().isEmpty()) {
            String academicYear = tc.getAcademicYear() != null ? tc.getAcademicYear() : "2025-26";
            tc.setTcNumber(generateTcNumber(academicYear));
        }
        return tcRepository.save(tc);
    }

    public String generateTcNumber(String academicYear) {
        long count = tcRepository.countByAcademicYear(academicYear) + 1;
        return String.format("TC-%s-%04d", academicYear, count);
    }
}
