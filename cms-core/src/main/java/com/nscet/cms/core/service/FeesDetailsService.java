package com.nscet.cms.core.service;

import com.nscet.cms.core.exception.ResourceNotFoundException;
import com.nscet.cms.db.entity.FeesDetails;
import com.nscet.cms.db.repository.FeesDetailsRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FeesDetailsService {

    private final FeesDetailsRepository repository;

    public FeesDetailsService(FeesDetailsRepository repository) {
        this.repository = repository;
    }

    public Page<FeesDetails> getAll(String search, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        if (search != null && !search.trim().isEmpty()) {
            return repository.search(search, pageable);
        }
        return repository.findAllActive(pageable);
    }

    public List<FeesDetails> getAllActiveList() {
        return repository.findAllActiveList();
    }

    public FeesDetails getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FeesDetails", "id", id));
    }

    @Transactional
    public FeesDetails create(FeesDetails feesDetails) {
        feesDetails.setIsActive(true);
        return repository.save(feesDetails);
    }

    @Transactional
    public FeesDetails update(Long id, FeesDetails updated) {
        FeesDetails existing = getById(id);
        existing.setFromDate(updated.getFromDate());
        existing.setToDate(updated.getToDate());
        existing.setDegree(updated.getDegree());
        existing.setSemester(updated.getSemester());
        existing.setQuota(updated.getQuota());
        existing.setDepartment(updated.getDepartment());
        existing.setDeptType(updated.getDeptType());
        existing.setAdmissionType(updated.getAdmissionType());
        existing.setFeesName(updated.getFeesName());
        existing.setAmount(updated.getAmount());
        return repository.save(existing);
    }

    @Transactional
    public void softDelete(Long id) {
        FeesDetails existing = getById(id);
        existing.setIsActive(false);
        repository.save(existing);
    }
}
