package com.nscet.cms.core.service;

import com.nscet.cms.core.exception.DuplicateResourceException;
import com.nscet.cms.core.exception.ResourceNotFoundException;
import com.nscet.cms.db.entity.BankMaster;
import com.nscet.cms.db.repository.BankMasterRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BankService {

    private final BankMasterRepository repository;

    public BankService(BankMasterRepository repository) {
        this.repository = repository;
    }

    public Page<BankMaster> getAll(String search, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        if (search != null && !search.trim().isEmpty()) {
            return repository.search(search, pageable);
        }
        return repository.findAllActive(pageable);
    }

    public BankMaster getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bank", "id", id));
    }

    @Transactional
    public BankMaster create(BankMaster bank) {
        bank.setIsActive(true);
        return repository.save(bank);
    }

    @Transactional
    public BankMaster update(Long id, BankMaster updated) {
        BankMaster existing = getById(id);
        existing.setBankShortName(updated.getBankShortName());
        existing.setAccountNumber(updated.getAccountNumber());
        existing.setBankName(updated.getBankName());
        existing.setBranch(updated.getBranch());
        existing.setIfscCode(updated.getIfscCode());
        existing.setRemarks(updated.getRemarks());
        return repository.save(existing);
    }

    @Transactional
    public void softDelete(Long id) {
        BankMaster existing = getById(id);
        existing.setIsActive(false);
        repository.save(existing);
    }
}
