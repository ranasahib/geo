/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rd.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 *
 * @author abhishek
 */
@Entity
public class RoleMenu {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    private String roleName;
    
    @ElementCollection
    List<String> menuNames = new ArrayList<>();

	public Long getId() {
		return id;
	}

	public String getRoleName() {
		return roleName;
	}

	public List<String> getMenuNames() {
		return menuNames;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public void setMenuNames(List<String> menuNames) {
		this.menuNames = menuNames;
	}

    
}
