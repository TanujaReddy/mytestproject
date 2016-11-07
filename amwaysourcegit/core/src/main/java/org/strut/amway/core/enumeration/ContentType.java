package org.strut.amway.core.enumeration;

public enum ContentType {

    APPLICATION_JSON("application/json");

    String name;

    ContentType(String name) {
        this.name = name;
    }

    public String getValue() {
        return this.name;
    }

}
