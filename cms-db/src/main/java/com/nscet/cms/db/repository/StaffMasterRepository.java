package com.nscet.cms.db.repository;

import com.nscet.cms.db.entity.StaffMaster;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StaffMasterRepository extends JpaRepository<StaffMaster, Long> {

    boolean existsByStaffCode(String staffCode);

    boolean existsByStaffCodeAndIdNot(String staffCode, Long id);

    @Query("SELECT s FROM StaffMaster s WHERE s.isActive = true")
    Page<StaffMaster> findAllActive(Pageable pageable);

    @Query("SELECT s FROM StaffMaster s WHERE s.isActive = true " +
           "AND (LOWER(s.staffCode) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(s.name) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<StaffMaster> search(@Param("search") String search, Pageable pageable);
}
