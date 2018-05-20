package com.rd.dto;

import java.util.List;
import java.util.Map;

public class ComplexQueryDTO {
	
	private String queryLayerName;
	private String joinOperationType; //AND (Intersect) OR (UNION)
    private List<String> queries;
    private List<String> refLayers; //optional
	public String getQueryLayerName() {
		return queryLayerName;
	}
	public void setQueryLayerName(String queryLayerName) {
		this.queryLayerName = queryLayerName;
	}
	public String getJoinOperationType() {
		return joinOperationType;
	}
	public void setJoinOperationType(String joinOperationType) {
		this.joinOperationType = joinOperationType;
	}
	public List<String> getQueries() {
		return queries;
	}
	public void setQueries(List<String> queries) {
		this.queries = queries;
	}
	public List<String> getRefLayers() {
		return refLayers;
	}
	public void setRefLayers(List<String> refLayers) {
		this.refLayers = refLayers;
	}
    
    
}
   
