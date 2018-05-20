/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rd.domain;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.Size;

/**
 *
 * @author abhishek
 */
@Entity
public class UserRole {

    @Id
    @Column(updatable = false, nullable = false)
    @Size(min = 0, max = 50)
    private String roleName;
    
    @Size(min = 0, max = 500)
    private String description;
    
    @Column(name = "status")
    private int status;
    @Column(name = "created_at")
    private Date createdAt;
    @Column(name = "updated_At")
    private Date updatedAt;

    public UserRole(String roleName) {
        this.roleName = roleName;
    }

    public UserRole() {
    }
    
    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

	@Override
	public boolean equals(Object obj) {
		UserRole userRole = (UserRole) obj;
		return (userRole.getRoleName().equals(this.roleName));
	}
    
    

}
