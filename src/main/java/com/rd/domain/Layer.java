package com.rd.domain;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity(name="layer")
public class Layer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;   
    
    @Column(name = "dataset_id")
    private String datasetId;
    
    @Column(name = "name")
    private String name;
    
    @Column(name = "count")
    private Integer count;
    
    @Column(name = "meta_data")
    private String metaData;
    
    @OneToMany
    List<LayerProperty> properties;
    
    private int status;
    
    @Column(name = "created_at")
    private Date createdAt;
    @Column(name = "update_at")
    private Date updatedAt;
    
	public Long getId() {
		return id;
	}
	public String getDatasetId() {
		return datasetId;
	}
	public String getName() {
		return name;
	}
	public Integer getCount() {
		return count;
	}
	public String getMetaData() {
		return metaData;
	}
	public List<LayerProperty> getProperties() {
		return properties;
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
	public void setId(Long id) {
		this.id = id;
	}
	public void setDatasetId(String datasetId) {
		this.datasetId = datasetId;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setCount(Integer count) {
		this.count = count;
	}
	public void setMetaData(String metaData) {
		this.metaData = metaData;
	}
	public void setProperties(List<LayerProperty> properties) {
		this.properties = properties;
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
