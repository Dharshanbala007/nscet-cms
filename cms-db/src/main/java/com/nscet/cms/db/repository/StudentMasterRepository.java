package com.nscet.cms.db.repository;

import com.nscet.cms.db.entity.StudentMaster;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentMasterRepository extends JpaRepository<StudentMaster, Long> {

    boolean existsByRollNumber(String rollNumber);

    boolean existsByAdmissionNo(String admissionNo);

    Optional<StudentMaster> findByRollNumber(String rollNumber);

    Optional<StudentMaster> findByAdmissionNo(String admissionNo);

    @Query("SELECT s FROM StudentMaster s WHERE s.isActive = true")
    Page<StudentMaster> findAllActive(Pageable pageable);

    @Query("SELECT s FROM StudentMaster s WHERE s.isActive = true AND s.rollNumber = :rollNumber")
    Optional<StudentMaster> findByRollNumberActive(@Param("rollNumber") String rollNumber);

    @Query("SELECT s FROM StudentMaster s WHERE s.isActive = true " +
           "AND (LOWER(s.rollNumber) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(s.name) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(s.admissionNo) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(s.registrationNo) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<StudentMaster> search(@Param("search") String search, Pageable pageable);
}
