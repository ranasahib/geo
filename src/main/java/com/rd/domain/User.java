package com.rd.domain;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.validation.constraints.Size;

import com.rd.encryption.DataConverter;

@Entity(name="geo_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String registryId;
    
    private String registryOrganisationId;
    
    @Size(min = 0, max = 50)
    private String loginName;
    
    @Convert(converter = DataConverter.class)   
	private String password;
    
    @Size(min = 0, max = 50)
    private String firstName;
    @Size(min = 0, max = 50)
    private String middleName;
    @Size(min = 0, max = 50)
    private String lastName;
    
    @Size(min = 0, max = 50)
    @Convert(converter = DataConverter.class)    
    private String pancard;
    
    @Size(min = 0, max = 50)
    @Convert(converter = DataConverter.class)     
    private String aadhar;
    
    @ManyToMany
    List<UserRole> userRoles;
    
    private String emailAddress;   
    private String mobileNumber; 
    
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

	public String getRegistryOrganisationId() {
        return registryOrganisationId;
    }

    public void setRegistryOrganisationId(String registryOrganisationId) {
        this.registryOrganisationId = registryOrganisationId;
    }
    
    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPancard() {
        return pancard;
    }

    public void setPancard(String pancard) {
        this.pancard = pancard;
    }

    public String getAadhar() {
        return aadhar;
    }

    public void setAadhar(String aadhar) {
        this.aadhar = aadhar;
    }

    public List<UserRole> getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(List<UserRole> userRoles) {
        this.userRoles = userRoles;
    }

   

    public String getEmailAddress() {
		return emailAddress;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
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

    

}
