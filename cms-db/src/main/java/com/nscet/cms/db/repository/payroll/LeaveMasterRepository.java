package com.nscet.cms.db.repository.payroll;

import com.nscet.cms.db.entity.payroll.LeaveMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LeaveMasterRepository extends JpaRepository<LeaveMaster, Long> {

    @Query("SELECT l FROM LeaveMaster l WHERE l.isActive = true ORDER BY l.id ASC")
    List<LeaveMaster> findAllActive();

    Optional<LeaveMaster> findByLeaveCode(String leaveCode);
}
