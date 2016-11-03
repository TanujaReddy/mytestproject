package org.strut.amway.core.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.Value;

import org.apache.sling.commons.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FamilySitesController {

    private static Logger LOGGER = LoggerFactory.getLogger(FamilySitesController.class);

    private static final String TITLE = "title";
    private static final String HREF = "href";
    private static final String MAP = "map";

    public FamilySitesController() {

    }

    public Map<String, String> getContent(Node currentNode) {

        Map<String, String> contentMap = new LinkedHashMap<String, String>();

        try {
            Property property = null;

            if (null != currentNode && currentNode.hasProperty(MAP)) {
                property = currentNode.getProperty(MAP);
            }

            if (null != property) {
                JSONObject obj = null;
                Value[] values = null;

                if (property.isMultiple()) {
                    values = property.getValues();
                } else {
                    values = new Value[1];
                    values[0] = property.getValue();
                }

                for (Value val : values) {
                    obj = new JSONObject(val.getString());
                    String value = String.valueOf(obj.get(HREF));
                    String key = String.valueOf(obj.get(TITLE));
                    contentMap.put(key, value);
                }
            }

        } catch (Exception e) {
            LOGGER.error("Cannot load Family Sites: " + e.getMessage(), e);
        }
        return contentMap;
    }

}
