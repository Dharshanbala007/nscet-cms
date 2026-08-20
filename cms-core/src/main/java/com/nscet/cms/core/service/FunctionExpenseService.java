package com.nscet.cms.core.service;

import com.nscet.cms.db.entity.FunctionExpense;
import com.nscet.cms.db.repository.FunctionExpenseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class FunctionExpenseService {

    private final FunctionExpenseRepository repository;

    public FunctionExpenseService(FunctionExpenseRepository repository) {
        this.repository = repository;
    }

    public List<FunctionExpense> getAllActive() {
        return repository.findAllActive();
    }

    public List<FunctionExpense> search(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllActive();
        }
        return repository.search(query.trim());
    }

    public Optional<FunctionExpense> getById(Long id) {
        return repository.findById(id);
    }

    @Transactional
    public FunctionExpense save(FunctionExpense entity) {
        if (entity.getAllocatedBudget() == null) entity.setAllocatedBudget(BigDecimal.ZERO);
        if (entity.getTotalExpense() == null) entity.setTotalExpense(BigDecimal.ZERO);
        
        // Auto-calculate balance
        entity.setBalanceAmount(entity.getAllocatedBudget().subtract(entity.getTotalExpense()));
        
        if (entity.getIsActive() == null) {
            entity.setIsActive(true);
        }
        return repository.save(entity);
    }

    @Transactional
    public void softDelete(Long id) {
        repository.findById(id).ifPresent(e -> {
            e.setIsActive(false);
            repository.save(e);
        });
    }
}
