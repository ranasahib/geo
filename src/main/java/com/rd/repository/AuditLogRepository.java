package com.rd.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rd.domain.AuditLog;
import com.rd.domain.Harvest;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

	List<AuditLog> findByUser(String user);
}
