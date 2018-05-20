package com.rd.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity(name="property")
public class LayerProperty {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;    

    @Column(name = "name")
    private String name;
    
    @Column(name = "default_value")
    private String defaultValue;
    
    @Column(name = "type")
    private String type;

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public String getType() {
		return type;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public void setType(String type) {
		this.type = type;
	}
    
    
}
