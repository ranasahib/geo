package com.rd.utils;

public enum ObjectType {
	Association ("urn:oasis:names:tc:ebxml-regrep:ObjectType:RegistryObject:Association"),
	Classification ("urn:oasis:names:tc:ebxml-regrep:ObjectType:RegistryObject:Classification"),
	ServiceProfile ("urn:ogc:def:ebRIM-ObjectType:OGC:ServiceProfile"),
	DataSet ("urn:ogc:def:ebRIM-ObjectType:OGC:Dataset");

    private final String name;       

    private ObjectType(String s) {
        name = s;
    }

    public boolean equalsName(String otherName) {
        return name.equals(otherName);
    }

    public String toString() {
       return this.name;
    }
}
