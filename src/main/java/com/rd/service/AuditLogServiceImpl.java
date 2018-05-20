package com.rd.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rd.domain.AuditLog;
import com.rd.repository.AuditLogRepository;

@Service
public class AuditLogServiceImpl implements AuditLogService{

	@Autowired
	AuditLogRepository repo;
	
	
	
	@Override
	public AuditLog findById(Long id) {
		// TODO Auto-generated method stub
		return repo.findOne(id);
	}

	@Override
	public List<AuditLog> findAll() {
		// TODO Auto-generated method stub
		return repo.findAll();
	}

	@Override
	public void persist(AuditLog log) {
		// TODO Auto-generated method stub
		repo.save(log);
	}

	@Override
	public List<AuditLog> findByUser(String user) {
		// TODO Auto-generated method stub
		return repo.findByUser(user);
	}

}
