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

import java.time.LocalDate;
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

    public Page<FeesDetails> getAll(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return repository.findAllActive(pageable);
    }

    public List<FeesDetails> findByDateRange(LocalDate fromDate, LocalDate toDate) {
        return repository.findByDateRange(fromDate, toDate);
    }

    public List<FeesDetails> findByCriteria(Integer semester, String degree, Long quotaId) {
        return repository.findByCriteria(semester, degree, quotaId);
    }

    public FeesDetails getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FeesDetails", "id", id));
    }

    @Transactional
    public FeesDetails create(FeesDetails details) {
        details.setIsActive(true);
        return repository.save(details);
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
