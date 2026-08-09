package com.nscet.cms.db.repository;

import com.nscet.cms.db.entity.FeeReceipt;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface FeeReceiptRepository extends JpaRepository<FeeReceipt, Long> {

    Optional<FeeReceipt> findByReceiptNumber(String receiptNumber);

    Page<FeeReceipt> findByStudentIdOrderByReceiptDateDesc(Long studentId, Pageable pageable);

    @Query("SELECT fr FROM FeeReceipt fr WHERE fr.isActive = true " +
           "AND fr.receiptDate BETWEEN :fromDate AND :toDate ORDER BY fr.receiptDate DESC")
    List<FeeReceipt> findByDateRange(@Param("fromDate") LocalDate fromDate,
                                     @Param("toDate") LocalDate toDate);

    @Query("SELECT COALESCE(SUM(fr.totalAmount), 0) FROM FeeReceipt fr " +
           "WHERE fr.isActive = true AND fr.receiptDate = :date")
    Optional<java.math.BigDecimal> sumTotalByDate(@Param("date") LocalDate date);

    @Query("SELECT COALESCE(SUM(fr.totalAmount), 0) FROM FeeReceipt fr " +
           "WHERE fr.isActive = true AND fr.baseAccount = :accountType AND fr.receiptDate = :date")
    BigDecimal sumByAccountTypeAndDate(@Param("accountType") String accountType,
                                       @Param("date") LocalDate date);

    long countByReceiptDate(LocalDate date);

    @Query("SELECT fr FROM FeeReceipt fr LEFT JOIN FETCH fr.student ORDER BY fr.receiptDate DESC")
    List<FeeReceipt> findTop10WithStudent();
}
