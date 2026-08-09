package com.nscet.cms.core.service;

import com.nscet.cms.db.entity.AuditLog;
import com.nscet.cms.db.repository.AuditLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    public AuditService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Transactional
    public void log(String action, String tableName, Long recordId, String oldValues, String newValues, Long userId) {
        AuditLog entry = new AuditLog();
        entry.setAction(action);
        entry.setTableName(tableName);
        entry.setRecordId(recordId);
        entry.setOldValues(oldValues);
        entry.setNewValues(newValues);
        entry.setUserId(userId);
        entry.setIpAddress("localhost");
        auditLogRepository.save(entry);
    }

    @Transactional
    public void logCreate(String tableName, Long recordId, String newValues, Long userId) {
        log("CREATE", tableName, recordId, null, newValues, userId);
    }

    @Transactional
    public void logUpdate(String tableName, Long recordId, String oldValues, String newValues, Long userId) {
        log("UPDATE", tableName, recordId, oldValues, newValues, userId);
    }

    @Transactional
    public void logDelete(String tableName, Long recordId, String oldValues, Long userId) {
        log("DELETE", tableName, recordId, oldValues, null, userId);
    }

    @Transactional
    public void logLogin(String username, boolean success, Long userId) {
        log(success ? "LOGIN_SUCCESS" : "LOGIN_FAILED", "admin_users", userId, null, username, userId);
    }
}
