package com.rd.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class OrganisationDTO {

	private long id;
    private String registryId;
    private String name;
    private String description;
    private List<EmailAddressDTO> emailAddress;
    private List<PhoneNumberDTO> phoneNumbers; 
    private List<AddressDTO> address;
    private UserDTO nodalOfficer;
    private UserDTO admin;
    private UserDTO user;
    
    private Long dataAccess;
   
    private int userRegistered;

    public long getId() {
		return id;
	}

	public void setId(long id) {
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

    public List<EmailAddressDTO> getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(List<EmailAddressDTO> emailAddress) {
        this.emailAddress = emailAddress;
    }
    
    public List<PhoneNumberDTO> getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(List<PhoneNumberDTO> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }

    public List<AddressDTO> getAddress() {
        return address;
    }

    public void setAddress(List<AddressDTO> address) {
        this.address = address;
    }

    public UserDTO getNodalOfficer() {
        return nodalOfficer;
    }

    public void setNodalOfficer(UserDTO nodalOfficer) {
        this.nodalOfficer = nodalOfficer;
    }

    public UserDTO getAdmin() {
        return admin;
    }

    public void setAdmin(UserDTO admin) {
        this.admin = admin;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

	public int getUserRegistered() {
		return userRegistered;
	}

	public void setUserRegistered(int userRegistered) {
		this.userRegistered = userRegistered;
	}

	public Long getDataAccess() {
		return dataAccess;
	}

	public void setDataAccess(Long dataAccess) {
		this.dataAccess = dataAccess;
	}
  
}
