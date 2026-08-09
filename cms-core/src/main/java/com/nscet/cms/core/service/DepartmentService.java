package com.nscet.cms.core.service;

import com.nscet.cms.core.exception.DuplicateResourceException;
import com.nscet.cms.core.exception.ResourceNotFoundException;
import com.nscet.cms.db.entity.DepartmentMaster;
import com.nscet.cms.db.repository.DepartmentMasterRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DepartmentService {

    private final DepartmentMasterRepository repository;

    public DepartmentService(DepartmentMasterRepository repository) {
        this.repository = repository;
    }

    public Page<DepartmentMaster> getAll(String search, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        if (search != null && !search.trim().isEmpty()) {
            return repository.search(search, pageable);
        }
        return repository.findAllActive(pageable);
    }

    public List<DepartmentMaster> getAllActive() {
        return repository.findAllActiveList();
    }

    public DepartmentMaster getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department", "id", id));
    }

    @Transactional
    public DepartmentMaster create(DepartmentMaster dept) {
        if (repository.existsByCode(dept.getCode())) {
            throw new DuplicateResourceException("Department", "code", dept.getCode());
        }
        dept.setIsActive(true);
        return repository.save(dept);
    }

    @Transactional
    public DepartmentMaster update(Long id, DepartmentMaster updated) {
        DepartmentMaster existing = getById(id);
        if (repository.existsByCodeAndIdNot(updated.getCode(), id)) {
            throw new DuplicateResourceException("Department", "code", updated.getCode());
        }
        existing.setCode(updated.getCode());
        existing.setShortName(updated.getShortName());
        existing.setName(updated.getName());
        existing.setType(updated.getType());
        return repository.save(existing);
    }

    @Transactional
    public void softDelete(Long id) {
        DepartmentMaster existing = getById(id);
        existing.setIsActive(false);
        repository.save(existing);
    }
}
