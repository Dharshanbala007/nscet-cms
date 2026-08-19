package com.nscet.cms.db.repository;

import com.nscet.cms.db.entity.PettyCashSuspense;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PettyCashSuspenseRepository extends JpaRepository<PettyCashSuspense, Long> {

    @Query("SELECT p FROM PettyCashSuspense p WHERE p.isActive = true")
    Page<PettyCashSuspense> findAllActive(Pageable pageable);

    @Query("SELECT p FROM PettyCashSuspense p WHERE p.isActive = true ORDER BY p.id DESC")
    List<PettyCashSuspense> findAllActiveList();

    @Query("SELECT p FROM PettyCashSuspense p WHERE p.isActive = true " +
           "AND (LOWER(p.voucherNo) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(p.staffName) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(p.department) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<PettyCashSuspense> search(@Param("search") String search, Pageable pageable);

    @Query("SELECT p FROM PettyCashSuspense p WHERE p.voucherDate = :date AND p.isActive = true")
    Page<PettyCashSuspense> findByVoucherDate(@Param("date") LocalDate date, Pageable pageable);

    @Query("SELECT p FROM PettyCashSuspense p WHERE p.voucherDate = :date AND p.isActive = true ORDER BY p.id DESC")
    List<PettyCashSuspense> findByVoucherDateList(@Param("date") LocalDate date);

    @Query("SELECT p.voucherNo FROM PettyCashSuspense p ORDER BY p.id DESC")
    Optional<String> findTopByOrderByVoucherNoDesc();
}
