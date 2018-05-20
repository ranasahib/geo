package com.rd.utils;

public enum SlotDataType {
	String ("urn:oasis:names:tc:ebxml-regrep:DataType:String");

    private final String name;       

    private SlotDataType(String s) {
        name = s;
    }

    public boolean equalsName(String otherName) {
        return name.equals(otherName);
    }

    public String toString() {
       return this.name;
    }
}
