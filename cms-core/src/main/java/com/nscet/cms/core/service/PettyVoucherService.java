package com.nscet.cms.core.service;

import com.nscet.cms.core.exception.ResourceNotFoundException;
import com.nscet.cms.db.entity.PettyVoucher;
import com.nscet.cms.db.entity.PettyVoucherItem;
import com.nscet.cms.db.repository.PettyVoucherRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PettyVoucherService {

    private final PettyVoucherRepository repository;

    public PettyVoucherService(PettyVoucherRepository repository) {
        this.repository = repository;
    }

    public List<PettyVoucher> getAll() {
        return repository.findAllActiveList();
    }

    public Page<PettyVoucher> getAll(String search, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        if (search != null && !search.trim().isEmpty()) {
            return repository.search(search, pageable);
        }
        return repository.findAllActive(pageable);
    }

    public PettyVoucher getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PettyVoucher", "id", id));
    }

    @Transactional
    public PettyVoucher create(PettyVoucher pettyVoucher) {
        pettyVoucher.setIsActive(true);
        pettyVoucher.setVoucherNo(generateNextVoucherNo());
        if (pettyVoucher.getItems() == null) {
            pettyVoucher.setItems(new ArrayList<>());
        }
        for (PettyVoucherItem item : pettyVoucher.getItems()) {
            pettyVoucher.addItem(item);
        }
        PettyVoucher saved = repository.save(pettyVoucher);
        recalculateTotalAmount(saved);
        return repository.save(saved);
    }

    @Transactional
    public PettyVoucher update(Long id, PettyVoucher updated) {
        PettyVoucher existing = getById(id);
        existing.setVoucherDate(updated.getVoucherDate());
        existing.setStaffName(updated.getStaffName());
        existing.setStaffCode(updated.getStaffCode());
        existing.setDesignation(updated.getDesignation());
        existing.setDepartment(updated.getDepartment());
        existing.setSuspenseVoucherNo(updated.getSuspenseVoucherNo());
        existing.setSuspenseDate(updated.getSuspenseDate());
        existing.setSuspenseAmount(updated.getSuspenseAmount());
        existing.setPurpose(updated.getPurpose());
        existing.getItems().clear();
        if (updated.getItems() != null) {
            for (PettyVoucherItem item : updated.getItems()) {
                existing.addItem(item);
            }
        }
        PettyVoucher saved = repository.save(existing);
        recalculateTotalAmount(saved);
        return repository.save(saved);
    }

    @Transactional
    public void softDelete(Long id) {
        PettyVoucher existing = getById(id);
        existing.setIsActive(false);
        repository.save(existing);
    }

    public Page<PettyVoucher> findByDate(LocalDate date, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return repository.findByVoucherDate(date, pageable);
    }

    public List<PettyVoucher> findByDate(LocalDate date) {
        return repository.findByVoucherDateList(date);
    }

    public PettyVoucher getByVoucherNo(String voucherNo) {
        return repository.findByVoucherNo(voucherNo)
                .orElseThrow(() -> new ResourceNotFoundException("PettyVoucher", "voucherNo", voucherNo));
    }

    public String generateNextVoucherNo() {
        try {
            Optional<String> lastVoucherNo = repository.findTopByOrderByVoucherNoDesc();
            if (lastVoucherNo.isPresent() && lastVoucherNo.get() != null) {
                String lastNo = lastVoucherNo.get();
                String numberPart = lastNo.replaceAll("[^0-9]", "");
                if (!numberPart.isEmpty()) {
                    int nextNumber = Integer.parseInt(numberPart) + 1;
                    return String.format("PV%03d", nextNumber);
                }
            }
        } catch (Exception e) {
            System.err.println("[PettyVoucherService] Error generating voucher no: " + e.getMessage());
        }
        return "PV001";
    }

    private void recalculateTotalAmount(PettyVoucher voucher) {
        BigDecimal total = voucher.getItems().stream()
                .map(PettyVoucherItem::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        voucher.setTotalAmount(total);
    }
}
