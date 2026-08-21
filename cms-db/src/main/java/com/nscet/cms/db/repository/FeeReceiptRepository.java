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
           "AND fr.receiptDate >= :fromDate AND fr.receiptDate <= :toDate ORDER BY fr.receiptDate DESC")
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

    @Query("SELECT fr FROM FeeReceipt fr LEFT JOIN FETCH fr.student WHERE fr.isActive = true " +
           "AND fr.receiptDate BETWEEN :fromDate AND :toDate ORDER BY fr.receiptDate DESC")
    Page<FeeReceipt> findByDateRangeWithStudent(@Param("fromDate") LocalDate fromDate,
                                                @Param("toDate") LocalDate toDate,
                                                Pageable pageable);

    @Query("SELECT fr FROM FeeReceipt fr LEFT JOIN FETCH fr.student WHERE fr.isActive = true " +
           "AND fr.receiptNumber = :receiptNo")
    Optional<FeeReceipt> findByReceiptNumberWithStudent(@Param("receiptNo") String receiptNo);

    @Query("SELECT fr FROM FeeReceipt fr LEFT JOIN FETCH fr.student WHERE fr.isActive = true " +
           "AND LOWER(fr.student.name) LIKE LOWER(CONCAT('%', :name, '%')) ORDER BY fr.receiptDate DESC")
    Page<FeeReceipt> findByStudentNameContaining(@Param("name") String name, Pageable pageable);

    @Query("SELECT fr FROM FeeReceipt fr LEFT JOIN FETCH fr.student WHERE fr.isActive = true " +
           "AND fr.student.rollNumber = :rollNo ORDER BY fr.receiptDate DESC")
    List<FeeReceipt> findByStudentRollNumber(@Param("rollNo") String rollNo);

    @Query("SELECT fr FROM FeeReceipt fr LEFT JOIN FETCH fr.student WHERE fr.isActive = true " +
           "AND fr.studentType = :studentType ORDER BY fr.receiptDate DESC")
    Page<FeeReceipt> findByStudentType(@Param("studentType") String studentType, Pageable pageable);

    @Query("SELECT fr FROM FeeReceipt fr LEFT JOIN FETCH fr.student WHERE fr.isActive = true " +
           "AND fr.baseAccount = :account ORDER BY fr.receiptDate DESC")
    Page<FeeReceipt> findByBaseAccount(@Param("account") String account, Pageable pageable);

    @Query("SELECT fr FROM FeeReceipt fr LEFT JOIN FETCH fr.student WHERE fr.isActive = true " +
           "AND fr.paymentMode = :paymentMode ORDER BY fr.receiptDate DESC")
    Page<FeeReceipt> findByPaymentMode(@Param("paymentMode") String paymentMode, Pageable pageable);

    @Query("SELECT fr FROM FeeReceipt fr LEFT JOIN FETCH fr.student WHERE fr.isActive = true " +
           "AND fr.receiptDate BETWEEN :fromDate AND :toDate " +
           "AND LOWER(fr.student.name) LIKE LOWER(CONCAT('%', :name, '%')) " +
           "AND fr.studentType = :studentType " +
           "AND fr.baseAccount = :account " +
           "AND fr.paymentMode = :paymentMode " +
           "ORDER BY fr.receiptDate DESC")
    Page<FeeReceipt> findFiltered(@Param("fromDate") LocalDate fromDate,
                                  @Param("toDate") LocalDate toDate,
                                  @Param("name") String name,
                                  @Param("studentType") String studentType,
                                  @Param("account") String account,
                                  @Param("paymentMode") String paymentMode,
                                  Pageable pageable);

    @Query("SELECT fr FROM FeeReceipt fr LEFT JOIN FETCH fr.student WHERE fr.isActive = true " +
           "AND fr.receiptDate >= :fromDate AND fr.receiptDate <= :toDate " +
           "AND (:name IS NULL OR :name = '' OR (fr.student IS NOT NULL AND LOWER(fr.student.name) LIKE LOWER(CONCAT('%', :name, '%')))) " +
           "AND (:studentType IS NULL OR :studentType = '' OR :studentType = 'All' OR fr.studentType = :studentType) " +
           "AND (:account IS NULL OR :account = '' OR :account = 'All' OR fr.baseAccount = :account) " +
           "AND (:paymentMode IS NULL OR :paymentMode = '' OR :paymentMode = 'All' OR fr.paymentMode = :paymentMode) " +
           "ORDER BY fr.receiptDate DESC")
    Page<FeeReceipt> findFilteredDynamic(@Param("fromDate") LocalDate fromDate,
                                         @Param("toDate") LocalDate toDate,
                                         @Param("name") String name,
                                         @Param("studentType") String studentType,
                                         @Param("account") String account,
                                         @Param("paymentMode") String paymentMode,
                                         Pageable pageable);

    @Query("SELECT COALESCE(SUM(fr.totalAmount), 0) FROM FeeReceipt fr WHERE fr.isActive = true " +
           "AND fr.receiptDate >= :fromDate AND fr.receiptDate <= :toDate " +
           "AND (:name IS NULL OR :name = '' OR (fr.student IS NOT NULL AND LOWER(fr.student.name) LIKE LOWER(CONCAT('%', :name, '%')))) " +
           "AND (:studentType IS NULL OR :studentType = '' OR :studentType = 'All' OR fr.studentType = :studentType) " +
           "AND (:account IS NULL OR :account = '' OR :account = 'All' OR fr.baseAccount = :account) " +
           "AND (:paymentMode IS NULL OR :paymentMode = '' OR :paymentMode = 'All' OR fr.paymentMode = :paymentMode)")
    BigDecimal sumFilteredDynamic(@Param("fromDate") LocalDate fromDate,
                                  @Param("toDate") LocalDate toDate,
                                  @Param("name") String name,
                                  @Param("studentType") String studentType,
                                  @Param("account") String account,
                                  @Param("paymentMode") String paymentMode);


    @Query("SELECT fr FROM FeeReceipt fr LEFT JOIN FETCH fr.student WHERE fr.isActive = true ORDER BY fr.receiptDate DESC")
    Page<FeeReceipt> findAllWithStudent(Pageable pageable);

    @Query("SELECT COALESCE(SUM(fr.totalAmount), 0) FROM FeeReceipt fr WHERE fr.isActive = true")
    BigDecimal sumAllAmounts();

    @Query("SELECT fr FROM FeeReceipt fr LEFT JOIN FETCH fr.student WHERE fr.isActive = true " +
           "AND fr.receiptDate BETWEEN :fromDate AND :toDate ORDER BY fr.receiptDate DESC")
    List<FeeReceipt> findByDateRangeWithStudentList(@Param("fromDate") LocalDate fromDate,
                                                     @Param("toDate") LocalDate toDate);
}
