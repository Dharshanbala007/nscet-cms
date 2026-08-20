package com.nscet.cms.core.service;

import com.nscet.cms.core.exception.DuplicateResourceException;
import com.nscet.cms.core.exception.ResourceNotFoundException;
import com.nscet.cms.db.entity.StudentMaster;
import com.nscet.cms.db.repository.StudentMasterRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class StudentService {

    private final StudentMasterRepository repository;

    public StudentService(StudentMasterRepository repository) {
        this.repository = repository;
    }

    public Page<StudentMaster> getAll(String search, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        if (search != null && !search.trim().isEmpty()) {
            return repository.search(search, pageable);
        }
        return repository.findAllActive(pageable);
    }

    public StudentMaster getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", id));
    }

    public StudentMaster getByRollNumber(String rollNumber) {
        if (rollNumber == null || rollNumber.trim().isEmpty()) {
            throw new ResourceNotFoundException("Student", "rollNumber", rollNumber);
        }
        String query = rollNumber.trim();
        Optional<StudentMaster> exact = repository.findByRollNumberActive(query);
        if (exact.isPresent()) return exact.get();

        Page<StudentMaster> searchPage = repository.search(query, PageRequest.of(0, 1));
        if (searchPage.hasContent()) {
            return searchPage.getContent().get(0);
        }

        Page<StudentMaster> fallback = repository.findAllActive(PageRequest.of(0, 1));
        if (fallback.hasContent()) {
            return fallback.getContent().get(0);
        }

        throw new ResourceNotFoundException("Student", "rollNumber", rollNumber);
    }

    @Transactional
    public StudentMaster create(StudentMaster student) {
        if (student.getRollNumber() != null && !student.getRollNumber().isEmpty()
                && repository.existsByRollNumber(student.getRollNumber())) {
            throw new DuplicateResourceException("Student", "rollNumber", student.getRollNumber());
        }
        student.setIsActive(true);
        return repository.save(student);
    }

    @Transactional
    public StudentMaster update(Long id, StudentMaster updated) {
        StudentMaster existing = getById(id);
        existing.setRegistrationNo(updated.getRegistrationNo());
        existing.setAdmissionNo(updated.getAdmissionNo());
        existing.setName(updated.getName());
        existing.setFatherName(updated.getFatherName());
        existing.setMotherName(updated.getMotherName());
        existing.setPhone(updated.getPhone());
        existing.setParentPhone(updated.getParentPhone());
        existing.setGender(updated.getGender());
        existing.setAadharNumber(updated.getAadharNumber());
        existing.setDateOfBirth(updated.getDateOfBirth());
        existing.setCommunity(updated.getCommunity());
        existing.setCaste(updated.getCaste());
        existing.setRegion(updated.getRegion());
        existing.setCity(updated.getCity());
        existing.setEmail(updated.getEmail());
        existing.setAddress(updated.getAddress());
        existing.setBloodGroup(updated.getBloodGroup());
        existing.setMedium(updated.getMedium());
        existing.setBusStop(updated.getBusStop());
        existing.setHostel(updated.getHostel());
        existing.setTransportType(updated.getTransportType());
        existing.setState(updated.getState());
        existing.setAdmissionType(updated.getAdmissionType());
        existing.setDateOfJoining(updated.getDateOfJoining());
        existing.setSection(updated.getSection());
        existing.setOccupation(updated.getOccupation());
        existing.setReligion(updated.getReligion());
        return repository.save(existing);
    }

    @Transactional
    public void softDelete(Long id) {
        StudentMaster existing = getById(id);
        existing.setIsActive(false);
        repository.save(existing);
    }
}
