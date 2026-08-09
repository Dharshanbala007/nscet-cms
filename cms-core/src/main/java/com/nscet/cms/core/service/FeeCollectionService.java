package com.nscet.cms.core.service;

import com.nscet.cms.core.exception.ResourceNotFoundException;
import com.nscet.cms.db.entity.FeeReceipt;
import com.nscet.cms.db.entity.FeeReceiptItem;
import com.nscet.cms.db.entity.StudentMaster;
import com.nscet.cms.db.repository.FeeReceiptRepository;
import com.nscet.cms.db.repository.StudentMasterRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class FeeCollectionService {

    private final FeeReceiptRepository receiptRepository;
    private final StudentMasterRepository studentRepository;

    public FeeCollectionService(FeeReceiptRepository receiptRepository,
                                 StudentMasterRepository studentRepository) {
        this.receiptRepository = receiptRepository;
        this.studentRepository = studentRepository;
    }

    public List<FeeReceipt> getReceiptsByStudent(Long studentId) {
        return receiptRepository.findByStudentIdOrderByReceiptDateDesc(studentId, PageRequest.of(0, 100)).getContent();
    }

    public List<FeeReceipt> getReceiptsByDateRange(LocalDate fromDate, LocalDate toDate) {
        return receiptRepository.findByDateRange(fromDate, toDate);
    }

    public FeeReceipt getById(Long id) {
        return receiptRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FeeReceipt", "id", id));
    }

    public FeeReceipt getByReceiptNumber(String receiptNumber) {
        return receiptRepository.findByReceiptNumber(receiptNumber)
                .orElseThrow(() -> new ResourceNotFoundException("FeeReceipt", "receiptNumber", receiptNumber));
    }

    @Transactional
    public FeeReceipt createReceipt(FeeReceipt receipt) {
        receipt.setReceiptNumber(generateReceiptNumber());
        receipt.setReceiptDate(LocalDate.now());
        receipt.setStatus("ACTIVE");

        BigDecimal total = receipt.getItems().stream()
                .map(FeeReceiptItem::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        receipt.setTotalAmount(total);

        return receiptRepository.save(receipt);
    }

    @Transactional
    public BigDecimal getCollectionByAccountAndDate(String accountType, LocalDate date) {
        return receiptRepository.sumByAccountTypeAndDate(accountType, date);
    }

    @Transactional
    public long getReceiptCountByDate(LocalDate date) {
        return receiptRepository.countByReceiptDate(date);
    }

    private String generateReceiptNumber() {
        String prefix = "MIS";
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yy-MM-dd"));
        long count = receiptRepository.countByReceiptDate(LocalDate.now());
        return String.format("%s-%s-%04d", prefix, dateStr, count + 1);
    }
}
