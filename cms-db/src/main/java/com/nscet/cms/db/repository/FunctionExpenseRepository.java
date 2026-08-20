package com.nscet.cms.db.repository;

import com.nscet.cms.db.entity.FunctionExpense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FunctionExpenseRepository extends JpaRepository<FunctionExpense, Long> {

    @Query("SELECT f FROM FunctionExpense f WHERE f.isActive = true ORDER BY f.expenseDate DESC")
    List<FunctionExpense> findAllActive();

    @Query("SELECT f FROM FunctionExpense f WHERE f.isActive = true AND " +
           "(LOWER(f.functionName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(f.department) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(f.status) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<FunctionExpense> search(@Param("query") String query);
}
