package com.rd.dto;

import java.util.List;
import java.util.Map;

public class HarvestDTO {

	private Long id;
	private Long orderId;
	
	private String source;
	private String resourceType;
	
	private String organisationId;
	private List<String> storeNames;
	private List<String> nodeNames;
	
	private long dataAccess;
	private long dataClassification;
	private long price;
	
	private String cost;
	private String year;
	private String locality;
	
	private List<UserDTO> users;
	
	private Integer updateSequence;
	
	private Map<String,LayerDTO> layers;

	public Long getId() {
		return id;
	}

	public Long getOrderId() {
		return orderId;
	}

	public String getSource() {
		return source;
	}

	public String getResourceType() {
		return resourceType;
	}

	public String getOrganisationId() {
		return organisationId;
	}

	public List<String> getStoreNames() {
		return storeNames;
	}

	public List<String> getNodeNames() {
		return nodeNames;
	}

	public long getDataAccess() {
		return dataAccess;
	}

	public long getDataClassification() {
		return dataClassification;
	}

	public long getPrice() {
		return price;
	}

	public String getCost() {
		return cost;
	}

	public String getYear() {
		return year;
	}

	public String getLocality() {
		return locality;
	}

	public List<UserDTO> getUsers() {
		return users;
	}

	public Integer getUpdateSequence() {
		return updateSequence;
	}

	public Map<String, LayerDTO> getLayers() {
		return layers;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}

	public void setOrganisationId(String organisationId) {
		this.organisationId = organisationId;
	}

	public void setStoreNames(List<String> storeNames) {
		this.storeNames = storeNames;
	}

	public void setNodeNames(List<String> nodeNames) {
		this.nodeNames = nodeNames;
	}

	public void setDataAccess(long dataAccess) {
		this.dataAccess = dataAccess;
	}

	public void setDataClassification(long dataClassification) {
		this.dataClassification = dataClassification;
	}

	public void setPrice(long price) {
		this.price = price;
	}

	public void setCost(String cost) {
		this.cost = cost;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public void setLocality(String locality) {
		this.locality = locality;
	}

	public void setUsers(List<UserDTO> users) {
		this.users = users;
	}

	public void setUpdateSequence(Integer updateSequence) {
		this.updateSequence = updateSequence;
	}

	public void setLayers(Map<String, LayerDTO> layers) {
		this.layers = layers;
	}

	
}
