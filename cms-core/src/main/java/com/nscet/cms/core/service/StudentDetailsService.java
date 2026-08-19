package com.nscet.cms.core.service;

import com.nscet.cms.core.exception.DuplicateResourceException;
import com.nscet.cms.core.exception.ResourceNotFoundException;
import com.nscet.cms.db.entity.StudentDetails;
import com.nscet.cms.db.repository.StudentDetailsRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StudentDetailsService {

    private final StudentDetailsRepository repository;

    public StudentDetailsService(StudentDetailsRepository repository) {
        this.repository = repository;
    }

    public Page<StudentDetails> getAll(String search, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        if (search != null && !search.trim().isEmpty()) {
            return repository.search(search, pageable);
        }
        return repository.findAllActive(pageable);
    }

    public StudentDetails getById(Long id) {
        return repository.findByIdActive(id)
                .orElseThrow(() -> new ResourceNotFoundException("StudentDetails", "id", id));
    }

    @Transactional
    public StudentDetails create(StudentDetails studentDetails) {
        if (studentDetails.getStudent() != null && studentDetails.getSemester() != null
                && studentDetails.getAcademicYear() != null) {
            long count = repository.countDuplicate(
                    studentDetails.getStudent().getId(),
                    studentDetails.getSemester(),
                    studentDetails.getAcademicYear());
            if (count > 0) {
                throw new DuplicateResourceException("StudentDetails", "student+semester+academicYear",
                        studentDetails.getStudent().getRollNumber() + " Sem " + studentDetails.getSemester());
            }
        }
        studentDetails.setIsActive(true);
        return repository.save(studentDetails);
    }

    @Transactional
    public StudentDetails update(Long id, StudentDetails updated) {
        StudentDetails existing = getById(id);
        if (updated.getStudent() != null && updated.getSemester() != null
                && updated.getAcademicYear() != null) {
            long count = repository.countDuplicateExcept(
                    updated.getStudent().getId(),
                    updated.getSemester(),
                    updated.getAcademicYear(),
                    id);
            if (count > 0) {
                throw new DuplicateResourceException("StudentDetails", "student+semester+academicYear",
                        updated.getStudent().getRollNumber() + " Sem " + updated.getSemester());
            }
        }
        existing.setStudent(updated.getStudent());
        existing.setSemester(updated.getSemester());
        existing.setCasteCategory(updated.getCasteCategory());
        existing.setBusStop(updated.getBusStop());
        existing.setHostel(updated.getHostel());
        existing.setTransportType(updated.getTransportType());
        existing.setState(updated.getState());
        existing.setAdmissionYear(updated.getAdmissionYear());
        existing.setSemType(updated.getSemType());
        existing.setDepartment(updated.getDepartment());
        existing.setQuota(updated.getQuota());
        existing.setDegree(updated.getDegree());
        existing.setSection(updated.getSection());
        existing.setAcademicYear(updated.getAcademicYear());
        return repository.save(existing);
    }

    @Transactional
    public void softDelete(Long id) {
        StudentDetails existing = getById(id);
        existing.setIsActive(false);
        repository.save(existing);
    }
}
