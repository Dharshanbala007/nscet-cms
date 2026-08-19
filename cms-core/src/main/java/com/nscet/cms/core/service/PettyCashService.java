package com.nscet.cms.core.service;

import com.nscet.cms.core.exception.ResourceNotFoundException;
import com.nscet.cms.db.entity.PettyCash;
import com.nscet.cms.db.repository.PettyCashRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class PettyCashService {

    private final PettyCashRepository repository;

    public PettyCashService(PettyCashRepository repository) {
        this.repository = repository;
    }

    public List<PettyCash> getAll() {
        return repository.findAllActiveList();
    }

    public Page<PettyCash> getAll(String search, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        if (search != null && !search.trim().isEmpty()) {
            return repository.search(search, pageable);
        }
        return repository.findAllActive(pageable);
    }

    public PettyCash getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PettyCash", "id", id));
    }

    @Transactional
    public PettyCash create(PettyCash pettyCash) {
        pettyCash.setIsActive(true);
        if (pettyCash.getVoucherNo() == null || pettyCash.getVoucherNo().isEmpty()) {
            pettyCash.setVoucherNo(generateNextVoucherNo());
        }
        return repository.save(pettyCash);
    }

    @Transactional
    public PettyCash update(Long id, PettyCash updated) {
        PettyCash existing = getById(id);
        existing.setVoucherDate(updated.getVoucherDate());
        existing.setStaffName(updated.getStaffName());
        existing.setDepartment(updated.getDepartment());
        existing.setAmount(updated.getAmount());
        existing.setPurpose(updated.getPurpose());
        existing.setTransactionType(updated.getTransactionType());
        existing.setRemarks(updated.getRemarks());
        return repository.save(existing);
    }

    @Transactional
    public void softDelete(Long id) {
        PettyCash existing = getById(id);
        existing.setIsActive(false);
        repository.save(existing);
    }

    public List<PettyCash> findByDate(LocalDate date) {
        return repository.findByVoucherDateList(date);
    }

    public BigDecimal getTotalCashBalance() {
        return repository.sumAllAmounts();
    }

    public String generateNextVoucherNo() {
        Optional<String> lastVoucherNo = repository.findTopByOrderByVoucherNoDesc();
        if (lastVoucherNo.isPresent()) {
            String lastNo = lastVoucherNo.get();
            String numberPart = lastNo.replaceAll("^PC", "");
            int nextNumber = Integer.parseInt(numberPart) + 1;
            return String.format("PC%03d", nextNumber);
        }
        return "PC001";
    }
}
