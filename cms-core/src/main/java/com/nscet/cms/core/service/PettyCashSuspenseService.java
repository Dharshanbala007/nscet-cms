package com.nscet.cms.core.service;

import com.nscet.cms.core.exception.ResourceNotFoundException;
import com.nscet.cms.db.entity.PettyCashSuspense;
import com.nscet.cms.db.repository.PettyCashSuspenseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class PettyCashSuspenseService {

    private final PettyCashSuspenseRepository repository;

    public PettyCashSuspenseService(PettyCashSuspenseRepository repository) {
        this.repository = repository;
    }

    public List<PettyCashSuspense> getAll() {
        return repository.findAllActiveList();
    }

    public Page<PettyCashSuspense> getAll(String search, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        if (search != null && !search.trim().isEmpty()) {
            return repository.search(search, pageable);
        }
        return repository.findAllActive(pageable);
    }

    public PettyCashSuspense getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PettyCashSuspense", "id", id));
    }

    @Transactional
    public PettyCashSuspense create(PettyCashSuspense entity) {
        entity.setIsActive(true);
        if (entity.getVoucherNo() == null || entity.getVoucherNo().isEmpty()) {
            entity.setVoucherNo(generateNextVoucherNo());
        }
        return repository.save(entity);
    }

    @Transactional
    public PettyCashSuspense update(Long id, PettyCashSuspense updated) {
        PettyCashSuspense existing = getById(id);
        existing.setVoucherDate(updated.getVoucherDate());
        existing.setCollegeOrHostel(updated.getCollegeOrHostel());
        existing.setStaffName(updated.getStaffName());
        existing.setDepartment(updated.getDepartment());
        existing.setDesignation(updated.getDesignation());
        existing.setAmount(updated.getAmount());
        existing.setAmountInWords(updated.getAmountInWords());
        existing.setPurpose(updated.getPurpose());
        return repository.save(existing);
    }

    @Transactional
    public void softDelete(Long id) {
        PettyCashSuspense existing = getById(id);
        existing.setIsActive(false);
        repository.save(existing);
    }

    public List<PettyCashSuspense> findByDate(LocalDate date) {
        return repository.findByVoucherDateList(date);
    }

    public String generateNextVoucherNo() {
        Optional<String> lastVoucherNo = repository.findTopByOrderByVoucherNoDesc();
        if (lastVoucherNo.isPresent()) {
            String lastNo = lastVoucherNo.get();
            String numberPart = lastNo.replaceAll("^PCS", "");
            int nextNumber = Integer.parseInt(numberPart) + 1;
            return String.format("PCS%03d", nextNumber);
        }
        return "PCS001";
    }
}
