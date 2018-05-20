package com.rd.service;

import java.util.List;

import com.rd.domain.AuditLog;

public interface AuditLogService {
	
	public AuditLog findById(Long id);
	public List<AuditLog> findAll();
	public void persist(AuditLog log);
	public List<AuditLog> findByUser(String user);

}
