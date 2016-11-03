package org.strut.amway.core.controller;

import java.util.Enumeration;
import java.util.ResourceBundle;

public class ResourceBundleTest extends ResourceBundle {

    @Override
    protected Object handleGetObject(String key) {

        if (key.equalsIgnoreCase("homePage")) {
            return "Home";
        }
        if (key.equalsIgnoreCase("maxLengthTitle")) {
            return "2";
        }
        if (key.equalsIgnoreCase("maxLengthCategory")) {
            return "3";
        }
        return null;
    }

    @Override
    public Enumeration<String> getKeys() {

        return null;
    }

}
