package com.nscet.cms.db.repository;

import com.nscet.cms.db.entity.DaySettlement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface DaySettlementRepository extends JpaRepository<DaySettlement, Long> {

    Optional<DaySettlement> findBySettlementDate(LocalDate date);
}
