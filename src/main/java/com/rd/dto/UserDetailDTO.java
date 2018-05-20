/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rd.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

/**
 *
 * @author abhishek
 */
@JsonInclude(Include.NON_NULL)
public class UserDetailDTO extends User {

    private static final long serialVersionUID = 1L;
   
    private String id;
    private String name;
    private String basicAuth;
    private String status;
    private List<String> roles;
    private String organisationRegistryId;
    private Map<String,Boolean> menus;
    
    public UserDetailDTO(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBasicAuth() {
        return basicAuth;
    }

    public void setBasicAuth(String basicAuth) {
        this.basicAuth = basicAuth;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

	public String getOrganisationRegistryId() {
		return organisationRegistryId;
	}

	public void setOrganisationRegistryId(String organisationRegistryId) {
		this.organisationRegistryId = organisationRegistryId;
	}

	public Map<String, Boolean> getMenus() {
		return menus;
	}

	public void setMenus(Map<String, Boolean> menus) {
		this.menus = menus;
	}
    
    

}
