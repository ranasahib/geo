package com.rd.dto.transaction;

import java.util.List;

public class Slot {
	private String name;
	private String slotType;
	private List<String> values;
	public String getName() {
		return name;
	}
	public String getSlotType() {
		return slotType;
	}
	public List<String> getValues() {
		return values;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setSlotType(String slotType) {
		this.slotType = slotType;
	}
	public void setValues(List<String> values) {
		this.values = values;
	}
	
	
}
