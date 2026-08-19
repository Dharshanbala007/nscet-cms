package com.nscet.cms.db.repository;

import com.nscet.cms.db.entity.PettyVoucher;
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
public interface PettyVoucherRepository extends JpaRepository<PettyVoucher, Long> {

    @Query("SELECT p FROM PettyVoucher p WHERE p.isActive = true")
    Page<PettyVoucher> findAllActive(Pageable pageable);

    @Query("SELECT p FROM PettyVoucher p WHERE p.isActive = true ORDER BY p.id DESC")
    List<PettyVoucher> findAllActiveList();

    @Query("SELECT p FROM PettyVoucher p WHERE p.isActive = true " +
           "AND (LOWER(p.voucherNo) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(p.staffName) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(p.department) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<PettyVoucher> search(@Param("search") String search, Pageable pageable);

    @Query("SELECT p FROM PettyVoucher p WHERE p.voucherDate = :date AND p.isActive = true")
    Page<PettyVoucher> findByVoucherDate(@Param("date") LocalDate date, Pageable pageable);

    @Query("SELECT p FROM PettyVoucher p WHERE p.voucherDate = :date AND p.isActive = true ORDER BY p.id DESC")
    List<PettyVoucher> findByVoucherDateList(@Param("date") LocalDate date);

    Optional<PettyVoucher> findByVoucherNo(String voucherNo);

    @Query("SELECT p.voucherNo FROM PettyVoucher p ORDER BY p.id DESC")
    Optional<String> findTopByOrderByVoucherNoDesc();
}
