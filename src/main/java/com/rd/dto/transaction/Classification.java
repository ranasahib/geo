package com.rd.dto.transaction;

public class Classification {
	
	private String id;
	
	private String classificationNode;
	private String classifiedObject;
    private String objectType;
    
    
    
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getClassificationNode() {
		return classificationNode;
	}
	public String getClassifiedObject() {
		return classifiedObject;
	}
	public String getObjectType() {
		return objectType;
	}
	public void setClassificationNode(String classificationNode) {
		this.classificationNode = classificationNode;
	}
	public void setClassifiedObject(String classifiedObject) {
		this.classifiedObject = classifiedObject;
	}
	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}
    
    
}
