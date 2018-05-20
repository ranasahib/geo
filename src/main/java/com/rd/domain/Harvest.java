package com.rd.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Harvest {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "order_id")
    private Long orderId;
    
    @Column(name = "organisation_id")
    private String organisationId;

    @Column(name = "harvest_id")
    private String harvestId;
    
    @Column(name = "data_id")
    private String dataId;
    
    private String objectType;
    private String type;
    
    @Column(name = "node_name")
    private String nodeName;
    
    @Column(name = "store_name")
    private String storeName;
    
    @Column(name = "url")
    private String url;
    @Column(name = "update_sequence")
    private Integer updateSequence;    

    @Column(name = "classification_ids")
    private String classificationIds;
    @Column(name = "association_ids")
    private String associationIds;
    
    
    private String content;
    
    @Column(name = "step")
    private Integer step;
    
    @Column(name = "status")
    private Integer status;
    @Column(name = "created_at")
    private Date createdAt;
    @Column(name = "updated_At")
    private Date updatedAt;
	public Long getId() {
		return id;
	}
	public Long getOrderId() {
		return orderId;
	}
	public String getOrganisationId() {
		return organisationId;
	}
	public String getHarvestId() {
		return harvestId;
	}
	public String getDataId() {
		return dataId;
	}
	public String getNodeName() {
		return nodeName;
	}
	public String getStoreName() {
		return storeName;
	}
	public String getUrl() {
		return url;
	}
	public Integer getUpdateSequence() {
		return updateSequence;
	}
	public String getClassificationIds() {
		return classificationIds;
	}
	public String getAssociationIds() {
		return associationIds;
	}
	public String getContent() {
		return content;
	}
	public Integer getStep() {
		return step;
	}
	public Integer getStatus() {
		return status;
	}
	public Date getCreatedAt() {
		return createdAt;
	}
	public Date getUpdatedAt() {
		return updatedAt;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}
	public void setOrganisationId(String organisationId) {
		this.organisationId = organisationId;
	}
	public void setHarvestId(String harvestId) {
		this.harvestId = harvestId;
	}
	public void setDataId(String dataId) {
		this.dataId = dataId;
	}
	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}
	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public void setUpdateSequence(Integer updateSequence) {
		this.updateSequence = updateSequence;
	}
	public void setClassificationIds(String classificationIds) {
		this.classificationIds = classificationIds;
	}
	public void setAssociationIds(String associationIds) {
		this.associationIds = associationIds;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public void setStep(Integer step) {
		this.step = step;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}
	public String getObjectType() {
		return objectType;
	}
	public String getType() {
		return type;
	}
	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}
	public void setType(String type) {
		this.type = type;
	}
        
}
