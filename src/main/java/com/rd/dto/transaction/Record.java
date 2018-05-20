/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rd.dto.transaction;

import com.rd.dto.AddressDTO;
import com.rd.dto.EmailAddressDTO;
import com.rd.dto.PhoneNumberDTO;
import java.util.List;
import java.util.Map;

/**
 *
 * @author abhishek
 */
public class Record {
	
    private String id;
    private String objectType;
    private String status;
    
    private List<Map<String,String>> name;
    private List<Map<String,String>> description;
    
    private Map<String,String> personName;
    private Map<String,String> versionInfo;
    
    private List<AddressDTO> addresses;
    private List<EmailAddressDTO> emailAddresses;
    private List<PhoneNumberDTO>  telephoneNumbers;
    
    private List<Slot> slots;
    
    private List<Classification> classifications;
    
    private String associationType;
    private String sourceObject;
    private String targetObject;
	public String getId() {
		return id;
	}
	public String getObjectType() {
		return objectType;
	}
	public String getStatus() {
		return status;
	}
	public List<Map<String, String>> getName() {
		return name;
	}
	public List<Map<String, String>> getDescription() {
		return description;
	}
	public Map<String, String> getPersonName() {
		return personName;
	}
	public Map<String, String> getVersionInfo() {
		return versionInfo;
	}
	public List<AddressDTO> getAddresses() {
		return addresses;
	}
	public List<EmailAddressDTO> getEmailAddresses() {
		return emailAddresses;
	}
	public List<PhoneNumberDTO> getTelephoneNumbers() {
		return telephoneNumbers;
	}
	public List<Slot> getSlots() {
		return slots;
	}
	public List<Classification> getClassifications() {
		return classifications;
	}
	public String getAssociationType() {
		return associationType;
	}
	public String getSourceObject() {
		return sourceObject;
	}
	public String getTargetObject() {
		return targetObject;
	}
	public void setId(String id) {
		this.id = id;
	}
	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public void setName(List<Map<String, String>> name) {
		this.name = name;
	}
	public void setDescription(List<Map<String, String>> description) {
		this.description = description;
	}
	public void setPersonName(Map<String, String> personName) {
		this.personName = personName;
	}
	public void setVersionInfo(Map<String, String> versionInfo) {
		this.versionInfo = versionInfo;
	}
	public void setAddresses(List<AddressDTO> addresses) {
		this.addresses = addresses;
	}
	public void setEmailAddresses(List<EmailAddressDTO> emailAddresses) {
		this.emailAddresses = emailAddresses;
	}
	public void setTelephoneNumbers(List<PhoneNumberDTO> telephoneNumbers) {
		this.telephoneNumbers = telephoneNumbers;
	}
	public void setSlots(List<Slot> slots) {
		this.slots = slots;
	}
	public void setClassifications(List<Classification> classifications) {
		this.classifications = classifications;
	}
	public void setAssociationType(String associationType) {
		this.associationType = associationType;
	}
	public void setSourceObject(String sourceObject) {
		this.sourceObject = sourceObject;
	}
	public void setTargetObject(String targetObject) {
		this.targetObject = targetObject;
	}    
}
