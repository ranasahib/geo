/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rd.repository;

import com.rd.domain.RoleMenu;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author abhishek
 */
public interface RoleMenuRepository extends JpaRepository<RoleMenu, String> {

	RoleMenu findByRoleName(String string);
	

}

