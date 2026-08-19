package com.nscet.cms.db.repository;

import com.nscet.cms.db.entity.PettyCash;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PettyCashRepository extends JpaRepository<PettyCash, Long> {

    @Query("SELECT p FROM PettyCash p WHERE p.isActive = true")
    Page<PettyCash> findAllActive(Pageable pageable);

    @Query("SELECT p FROM PettyCash p WHERE p.isActive = true ORDER BY p.id DESC")
    List<PettyCash> findAllActiveList();

    @Query("SELECT p FROM PettyCash p WHERE p.isActive = true " +
           "AND (LOWER(p.voucherNo) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(p.staffName) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(p.department) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<PettyCash> search(@Param("search") String search, Pageable pageable);

    @Query("SELECT p FROM PettyCash p WHERE p.voucherDate = :date AND p.isActive = true")
    Page<PettyCash> findByVoucherDate(@Param("date") LocalDate date, Pageable pageable);

    @Query("SELECT p FROM PettyCash p WHERE p.voucherDate = :date AND p.isActive = true ORDER BY p.id DESC")
    List<PettyCash> findByVoucherDateList(@Param("date") LocalDate date);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM PettyCash p WHERE p.isActive = true")
    BigDecimal sumAllAmounts();

    @Query("SELECT p.voucherNo FROM PettyCash p ORDER BY p.id DESC")
    Optional<String> findTopByOrderByVoucherNoDesc();
}
