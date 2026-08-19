package com.nscet.cms.core.service;

import com.nscet.cms.core.exception.ResourceNotFoundException;
import com.nscet.cms.db.entity.QuotaMaster;
import com.nscet.cms.db.repository.QuotaMasterRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class QuotaService {

    private final QuotaMasterRepository repository;

    public QuotaService(QuotaMasterRepository repository) {
        this.repository = repository;
    }

    public List<QuotaMaster> getAllActive() {
        return repository.findAllActiveList();
    }

    public Page<QuotaMaster> getAll(String search, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        if (search != null && !search.trim().isEmpty()) {
            return repository.search(search, pageable);
        }
        return repository.findAllActive(pageable);
    }

    public QuotaMaster getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quota", "id", id));
    }

    @Transactional
    public QuotaMaster create(QuotaMaster quota) {
        quota.setIsActive(true);
        return repository.save(quota);
    }

    @Transactional
    public QuotaMaster update(Long id, QuotaMaster updated) {
        QuotaMaster existing = getById(id);
        existing.setCode(updated.getCode());
        existing.setName(updated.getName());
        existing.setPercentage(updated.getPercentage());
        existing.setAmount(updated.getAmount());
        existing.setDiscountAmount(updated.getDiscountAmount());
        existing.setAdmissionType(updated.getAdmissionType());
        return repository.save(existing);
    }

    @Transactional
    public void softDelete(Long id) {
        QuotaMaster existing = getById(id);
        existing.setIsActive(false);
        repository.save(existing);
    }
}
