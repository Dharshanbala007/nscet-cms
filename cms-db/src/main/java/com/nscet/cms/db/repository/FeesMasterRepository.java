package com.nscet.cms.db.repository;

import com.nscet.cms.db.entity.FeesMaster;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FeesMasterRepository extends JpaRepository<FeesMaster, Long> {

    @Query("SELECT f FROM FeesMaster f WHERE f.isActive = true")
    Page<FeesMaster> findAllActive(Pageable pageable);

    @Query("SELECT f FROM FeesMaster f WHERE f.isActive = true ORDER BY f.name")
    java.util.List<FeesMaster> findAllActiveList();

    @Query("SELECT f FROM FeesMaster f WHERE f.isActive = true AND f.semesterFee = true ORDER BY f.name")
    java.util.List<FeesMaster> findSemesterFees();

    @Query("SELECT f FROM FeesMaster f WHERE f.isActive = true AND f.semesterFee = false ORDER BY f.name")
    java.util.List<FeesMaster> findOtherFees();

    @Query("SELECT f FROM FeesMaster f WHERE f.isActive = true " +
           "AND (LOWER(f.name) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(f.feesGroup) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<FeesMaster> search(@Param("search") String search, Pageable pageable);
}
