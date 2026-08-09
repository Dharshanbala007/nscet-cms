package com.nscet.cms.db.repository;

import com.nscet.cms.db.entity.BankMaster;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BankMasterRepository extends JpaRepository<BankMaster, Long> {

    @Query("SELECT b FROM BankMaster b WHERE b.isActive = true")
    Page<BankMaster> findAllActive(Pageable pageable);

    @Query("SELECT b FROM BankMaster b WHERE b.isActive = true " +
           "AND (LOWER(b.bankShortName) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(b.bankName) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR b.accountNumber LIKE CONCAT('%', :search, '%'))")
    Page<BankMaster> search(@Param("search") String search, Pageable pageable);
}
