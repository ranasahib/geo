package com.rd.utils;

public enum StatusType {
	Submitted ("urn:oasis:names:tc:ebxml-regrep:StatusType:Submitted");

    private final String name;       

    private StatusType(String s) {
        name = s;
    }

    public boolean equalsName(String otherName) {
        return name.equals(otherName);
    }

    public String toString() {
       return this.name;
    }
}
