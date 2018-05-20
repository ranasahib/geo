/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rd.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.rd.dto.OrganisationDTO;
import com.rd.dto.ResponseWrapper;
import com.rd.dto.UserDetailDTO;
import com.rd.repository.RoleMenuRepository;
import com.rd.service.CommonService;
import com.rd.service.OrganisationService;
import com.rd.service.UserService;

/**
 *
 * @author abhishek
 */ 
@Controller
@RequestMapping("/secure/registry")
public class RegistryAdminController {
    
	@Autowired
	CommonService commonService;

    @Autowired
    UserService userService;
    
    @Autowired
    OrganisationService organisationService;
    
    @Autowired
    RoleMenuRepository roleMenuRepository;
    
    @CrossOrigin(origins = "*")
    @RequestMapping(value = {"/addOrganisation"}, method = RequestMethod.POST)
    public ResponseEntity<?> addOrganisation(@RequestBody OrganisationDTO organisationDTO) {
        ResponseWrapper responseWrapper = organisationService.addOrganisation(organisationDTO);
        HttpHeaders headers = new HttpHeaders();
        headers.set("result", "success");
        return new ResponseEntity<>(responseWrapper, headers, HttpStatus.OK);
    }
    
    @CrossOrigin(origins = "*")
    @RequestMapping(value = {"/createOrganisation"}, method = RequestMethod.POST)
    public ResponseEntity<?> createOrganisation(@RequestBody OrganisationDTO organisationDTO) {
        UserDetailDTO userDetailDTO = (UserDetailDTO) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        ResponseWrapper responseWrapper = organisationService.createOrganisation(organisationDTO,userDetailDTO);
        HttpHeaders headers = new HttpHeaders();
        headers.set("result", "success");
        return new ResponseEntity<>(responseWrapper, headers, HttpStatus.OK);
    }
    
    @CrossOrigin(origins = "*")
    @RequestMapping(value = {"/associateToOrganisation/{organisationId}/{userId}"}, method = RequestMethod.POST)
    public ResponseEntity<?> associateToOrganisation(@PathVariable String organisationId,@PathVariable String userId) {
        UserDetailDTO userDetailDTO = (UserDetailDTO) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        ResponseWrapper responseWrapper = userService.associateToOrganisation(organisationId,userId,userDetailDTO);
        HttpHeaders headers = new HttpHeaders();
        headers.set("result", "success");
        return new ResponseEntity<>(responseWrapper, headers, HttpStatus.OK);
    }
    
    @RequestMapping(value = {"/approveOrganisation/{id}"}, method = RequestMethod.POST)
    public ResponseEntity<?> approve(@PathVariable Long id) {
        UserDetailDTO userDetailDTO = (UserDetailDTO) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        ResponseWrapper responseWrapper = organisationService.approveOrganisation(id, userDetailDTO);
        HttpHeaders headers = new HttpHeaders();
        headers.set("result", "success");
        return new ResponseEntity<>(responseWrapper, headers, HttpStatus.OK);
    }
    
    @RequestMapping(value = {"/list/inactiveOrganisation"}, method = RequestMethod.GET)
    public ResponseEntity<?> inactiveOrganisation() {
        UserDetailDTO userDetailDTO = (UserDetailDTO) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        ResponseWrapper responseWrapper = organisationService.inactiveOrganisation(userDetailDTO);
        HttpHeaders headers = new HttpHeaders();
        headers.set("result", "success");
        return new ResponseEntity<>(responseWrapper, headers, HttpStatus.OK);
    }
    
	

    @RequestMapping(value = {"/createRole"}, method = RequestMethod.POST)
    public ResponseEntity<?> createRole(@RequestBody Map<String, Object> input) throws Exception {
    	UserDetailDTO userDetailDTO = (UserDetailDTO) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();    	
    	ResponseWrapper responseWrapper = userService.createRole(input,userDetailDTO);
        HttpHeaders headers = new HttpHeaders();
        headers.set("result", "success");
        return new ResponseEntity<>(responseWrapper, headers, HttpStatus.OK);
    }
    
    
    
    @RequestMapping(value = {"/deleteRole"}, method = RequestMethod.POST)
    public ResponseEntity<?> deleteRole(@RequestBody Map<String, Object> input) throws Exception {
        UserDetailDTO userDetailDTO = (UserDetailDTO) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        String output = commonService.operation("urm", input, "deleteRole", userDetailDTO);
        HttpHeaders headers = new HttpHeaders();
        headers.set("result", "success");
        return new ResponseEntity<>(output, headers, HttpStatus.OK);
    }
    
    @RequestMapping(value = {"/role/list/{roleName}"}, method = RequestMethod.GET)
    public ResponseEntity<?> rolePermission(@PathVariable String roleName) {
        UserDetailDTO userDetailDTO = (UserDetailDTO) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        ResponseWrapper responseWrapper = userService.getRolePermission(roleName);
        HttpHeaders headers = new HttpHeaders();
        headers.set("result", "success");
        return new ResponseEntity<>(responseWrapper, headers, HttpStatus.OK);
    }
    
    @RequestMapping(value = {"/role/add/{roleName}"}, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addRolePermission(@PathVariable String roleName,@RequestBody Map<String,Boolean> menus) {
        UserDetailDTO userDetailDTO = (UserDetailDTO) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        ResponseWrapper responseWrapper = userService.addRolePermission(roleName,menus);
        HttpHeaders headers = new HttpHeaders();
        headers.set("result", "success");
        return new ResponseEntity<>(responseWrapper, headers, HttpStatus.OK);
    }


}
