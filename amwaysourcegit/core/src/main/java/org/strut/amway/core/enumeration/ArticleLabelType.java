package org.strut.amway.core.enumeration;

import java.util.ArrayList;
import java.util.List;

public enum ArticleLabelType {

    PUBLIC("Public"), PRIVATE("Private"), IBO("IBO"), CLIENT("Client");

    private String label;

    ArticleLabelType(final String label) {
        this.label = label;
    }

    public String getLabel() {
        return this.label;
    }

    public static ArticleLabelType resolve(String label) {
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
            throw new IllegalArgumentException("Invalid Article Label " + label);
        }
    }

    public static List<ArticleLabelType> resolve(String[] labels) {
        final List<ArticleLabelType> articleLabelTypes = new ArrayList<ArticleLabelType>();
        if (labels == null || labels.length == 0) {
            return articleLabelTypes;
        }

        for (String label : labels) {
            articleLabelTypes.add(resolve(label));
        }
        return articleLabelTypes;
    }

}
