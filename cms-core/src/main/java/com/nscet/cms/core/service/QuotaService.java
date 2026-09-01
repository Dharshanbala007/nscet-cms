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
import java.util.Optional;

@Service
public class QuotaService {

    private final QuotaMasterRepository repository;

    public QuotaService(QuotaMasterRepository repository) {
        this.repository = repository;
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

    public Optional<QuotaMaster> findByCode(String code) {
        return repository.findByCode(code);
    }

    public String generateNextCode() {
        List<QuotaMaster> all = repository.findAll();
        int maxCode = 0;
        for (QuotaMaster q : all) {
            if (q.getCode() != null) {
                try {
                    int val = Integer.parseInt(q.getCode().trim());
                    if (val > maxCode) maxCode = val;
                } catch (NumberFormatException ignored) {}
            }
        }
        if (maxCode == 0) maxCode = 217;
        return String.format("%04d", maxCode + 1);
    }

    @Transactional
    public QuotaMaster create(QuotaMaster quota) {
        quota.setIsActive(true);
        if (quota.getCode() == null || quota.getCode().trim().isEmpty() || repository.existsByCode(quota.getCode().trim())) {
            quota.setCode(generateNextCode());
        }
        return repository.save(quota);
    }

    @Transactional
    public QuotaMaster update(Long id, QuotaMaster updated) {
        QuotaMaster existing = getById(id);
        if (updated.getCode() != null && !updated.getCode().trim().isEmpty()) {
            Optional<QuotaMaster> byCode = repository.findByCode(updated.getCode().trim());
            if (byCode.isPresent() && !byCode.get().getId().equals(id)) {
                // Code belongs to another record, keep existing code to prevent duplicate key constraint
            } else {
                existing.setCode(updated.getCode().trim());
            }
        }
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
