package com.nscet.cms.db.repository;

import com.nscet.cms.db.entity.TransferCertificate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransferCertificateRepository extends JpaRepository<TransferCertificate, Long> {

    Optional<TransferCertificate> findByTcNumber(String tcNumber);

    @Query("SELECT COUNT(tc) FROM TransferCertificate tc WHERE tc.academicYear = :year")
    long countByAcademicYear(String year);
}
