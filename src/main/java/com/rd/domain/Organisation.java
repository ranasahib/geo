package com.rd.domain;

import java.util.Date;
import java.util.List;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Entity
public class Organisation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @Column(nullable = true)
    @Size(min = 0, max = 50)
    private String registryId;

    @Size(min = 0, max = 20)
    private String name;
    
    @Size(min = 0, max = 500)
    private String description;
    
    @OneToMany
    private List<User> users;
    
    @Column(name = "status")
    private int status;
    @Column(name = "created_at")
    private Date createdAt;
    @Column(name = "updated_At")
    private Date updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRegistryId() {
        return registryId;
    }

    public void setRegistryId(String registryId) {
        this.registryId = registryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

	public List<User> getUsers() {
		return users;
	}

	public int getStatus() {
		return status;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}
    
    
}
