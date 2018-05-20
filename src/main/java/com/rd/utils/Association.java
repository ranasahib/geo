package com.rd.utils;

public enum Association {
	
	Owner ("urn:oasis:names:tc:ebxml-regrep:AssociationType:OwnerOf"),
	AccessControlPolicy ("urn:oasis:names:tc:ebxml-regrep:AssociationType:AccessControlPolicyFor"),
	Employee ("urn:oasis:names:tc:ebxml-regrep:AssociationType:AffiliatedWith:EmployeeOf");

    private final String name;       

    private Association(String s) {
        name = s;
    }

    public boolean equalsName(String otherName) {
        return name.equals(otherName);
    }

    public String toString() {
       return this.name;
    }
}
