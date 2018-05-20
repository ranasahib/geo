package com.rd.dto;

import java.util.List;
import java.util.Map;

public class LayerDTO {
	
	private Long id; 
	private String datasetId;
	private String name;
    private Integer count;
    private Map<String,String> metaData;
    
    private List<PropertyDTO> properties;

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

	public Map<String, String> getMetaData() {
		return metaData;
	}

	public List<PropertyDTO> getProperties() {
		return properties;
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

	public void setMetaData(Map<String, String> metaData) {
		this.metaData = metaData;
	}

	public void setProperties(List<PropertyDTO> properties) {
		this.properties = properties;
	}
}
