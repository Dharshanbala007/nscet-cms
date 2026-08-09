package com.nscet.cms.core.service;

import com.nscet.cms.core.exception.DuplicateResourceException;
import com.nscet.cms.core.exception.ResourceNotFoundException;
import com.nscet.cms.db.entity.DesignationMaster;
import com.nscet.cms.db.repository.DesignationMasterRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DesignationService {

    private final DesignationMasterRepository repository;

    public DesignationService(DesignationMasterRepository repository) {
        this.repository = repository;
    }

    public Page<DesignationMaster> getAll(String search, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        if (search != null && !search.trim().isEmpty()) {
            return repository.search(search, pageable);
        }
        return repository.findAllActive(pageable);
    }

    public DesignationMaster getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Designation", "id", id));
    }

    @Transactional
    public DesignationMaster create(DesignationMaster designation) {
        if (repository.existsByCode(designation.getCode())) {
            throw new DuplicateResourceException("Designation", "code", designation.getCode());
        }
        designation.setIsActive(true);
        return repository.save(designation);
    }

    @Transactional
    public DesignationMaster update(Long id, DesignationMaster updated) {
        DesignationMaster existing = getById(id);
        if (repository.existsByCodeAndIdNot(updated.getCode(), id)) {
            throw new DuplicateResourceException("Designation", "code", updated.getCode());
        }
        existing.setCode(updated.getCode());
        existing.setShortName(updated.getShortName());
        existing.setName(updated.getName());
        existing.setCategory(updated.getCategory());
        existing.setColorCode(updated.getColorCode());
        return repository.save(existing);
    }

    @Transactional
    public void softDelete(Long id) {
        DesignationMaster existing = getById(id);
        existing.setIsActive(false);
        repository.save(existing);
    }
}
