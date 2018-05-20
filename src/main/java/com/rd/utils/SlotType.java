package com.rd.utils;

public enum SlotType {
	StoreName ("StoreName"),
	NodeName ("NodeName"),
	Cost ("Cost"),
	Year ("Year"),
	Locality ("Locality");

    private final String name;       

    private SlotType(String s) {
        name = s;
    }

    public boolean equalsName(String otherName) {
        return name.equals(otherName);
    }

    public String toString() {
       return this.name;
    }
}
