package com.nscet.cms.core.service;

import com.nscet.cms.core.exception.ResourceNotFoundException;
import com.nscet.cms.db.entity.FeesMaster;
import com.nscet.cms.db.repository.FeesMasterRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FeesService {

    private final FeesMasterRepository repository;

    public FeesService(FeesMasterRepository repository) {
        this.repository = repository;
    }

    public Page<FeesMaster> getAll(String search, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        if (search != null && !search.trim().isEmpty()) {
            return repository.search(search, pageable);
        }
        return repository.findAllActive(pageable);
    }

    public List<FeesMaster> getAllActiveList() {
        return repository.findAllActiveList();
    }

    public List<FeesMaster> getSemesterFees() {
        return repository.findSemesterFees();
    }

    public List<FeesMaster> getOtherFees() {
        return repository.findOtherFees();
    }

    public FeesMaster getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FeesMaster", "id", id));
    }

    @Transactional
    public FeesMaster create(FeesMaster fees) {
        fees.setIsActive(true);
        return repository.save(fees);
    }

    @Transactional
    public FeesMaster update(Long id, FeesMaster updated) {
        FeesMaster existing = getById(id);
        existing.setName(updated.getName());
        existing.setFeesGroup(updated.getFeesGroup());
        existing.setFromDate(updated.getFromDate());
        existing.setToDate(updated.getToDate());
        existing.setSemesterFee(updated.getSemesterFee());
        return repository.save(existing);
    }

    @Transactional
    public void softDelete(Long id) {
        FeesMaster existing = getById(id);
        existing.setIsActive(false);
        repository.save(existing);
    }
}
