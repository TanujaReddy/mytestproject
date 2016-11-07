package org.strut.amway.core.enumeration;

import java.util.ArrayList;
import java.util.List;

public enum IframeLabelType {

    PUBLIC("Public"), PRIVATE("Private"), IBO("IBO"), CLIENT("Client");

    private String label;

    IframeLabelType(final String label) {
        this.label = label;
    }

    public String getLabel() {
        return this.label;
    }

    public static IframeLabelType resolve(String label) {
        switch (label) {
        case "Public":
            return PUBLIC;

        case "Private":
            return PRIVATE;

        case "IBO":
            return IBO;

        case "Client":
            return CLIENT;

        default:
            throw new IllegalArgumentException("Invalid Iframe Label " + label);
        }
    }

    public static List<IframeLabelType> resolve(String[] labels) {
        final List<IframeLabelType> iframeLabelTypes = new ArrayList<IframeLabelType>();
        if (labels == null || labels.length == 0) {
            return iframeLabelTypes;
        }

        for (String label : labels) {
            iframeLabelTypes.add(resolve(label));
        }
        return iframeLabelTypes;
    }

}
