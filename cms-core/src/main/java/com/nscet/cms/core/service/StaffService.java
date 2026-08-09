package com.nscet.cms.core.service;

import com.nscet.cms.core.exception.DuplicateResourceException;
import com.nscet.cms.core.exception.ResourceNotFoundException;
import com.nscet.cms.db.entity.StaffMaster;
import com.nscet.cms.db.repository.StaffMasterRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StaffService {

    private final StaffMasterRepository repository;

    public StaffService(StaffMasterRepository repository) {
        this.repository = repository;
    }

    public Page<StaffMaster> getAll(String search, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        if (search != null && !search.trim().isEmpty()) {
            return repository.search(search, pageable);
        }
        return repository.findAllActive(pageable);
    }

    public StaffMaster getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Staff", "id", id));
    }

    @Transactional
    public StaffMaster create(StaffMaster staff) {
        if (repository.existsByStaffCode(staff.getStaffCode())) {
            throw new DuplicateResourceException("Staff", "staffCode", staff.getStaffCode());
        }
        staff.setIsActive(true);
        return repository.save(staff);
    }

    @Transactional
    public StaffMaster update(Long id, StaffMaster updated) {
        StaffMaster existing = getById(id);
        if (repository.existsByStaffCodeAndIdNot(updated.getStaffCode(), id)) {
            throw new DuplicateResourceException("Staff", "staffCode", updated.getStaffCode());
        }
        existing.setStaffCode(updated.getStaffCode());
        existing.setName(updated.getName());
        existing.setAddress(updated.getAddress());
        existing.setCity(updated.getCity());
        existing.setPinCode(updated.getPinCode());
        existing.setDateOfBirth(updated.getDateOfBirth());
        existing.setCategory(updated.getCategory());
        existing.setDepartment(updated.getDepartment());
        existing.setDesignation(updated.getDesignation());
        existing.setStaffGroup(updated.getStaffGroup());
        existing.setCollegeCode(updated.getCollegeCode());
        existing.setTransport(updated.getTransport());
        existing.setEmail(updated.getEmail());
        existing.setPfActive(updated.getPfActive());
        existing.setSex(updated.getSex());
        existing.setDateOfJoining(updated.getDateOfJoining());
        existing.setPhone(updated.getPhone());
        existing.setBloodGroup(updated.getBloodGroup());
        existing.setAadharNumber(updated.getAadharNumber());
        existing.setPanNumber(updated.getPanNumber());
        existing.setEsslId(updated.getEsslId());
        return repository.save(existing);
    }

    @Transactional
    public void softDelete(Long id) {
        StaffMaster existing = getById(id);
        existing.setIsActive(false);
        repository.save(existing);
    }
}
