package com.rd.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.rd.domain.AuditLog;
import com.rd.service.AuditLogService;

@Controller
@RequestMapping("/audit")
public class AuditLogController {
	@Autowired
	AuditLogService service;
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = {"/id"}, method = RequestMethod.GET)
	public AuditLog findById(@RequestParam Long id){
		return service.findById(id);
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = {"/list"}, method = RequestMethod.GET)
	public List<AuditLog> findAll(){
		return service.findAll();
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = {"/save"}, method = RequestMethod.POST)
	public void save(@RequestBody AuditLog log){
		service.persist(log);

	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = {"/byUser"}, method = RequestMethod.GET)
	public List<AuditLog> findByUser(String user){
		return service.findByUser(user);
	}
	
	
	
	
}
