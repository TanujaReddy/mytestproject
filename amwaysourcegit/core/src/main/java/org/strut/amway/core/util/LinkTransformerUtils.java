package org.strut.amway.core.util;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LinkTransformerUtils {

    private static Logger LOGGER = LoggerFactory.getLogger(LinkTransformerUtils.class);

    public static String transformPath(final ResourceResolver resourceResolver, final String path) throws URISyntaxException {
        if (StringUtils.isEmpty(path)) {
            return null;
        }

        String mappedPath = resourceResolver.map(path);
        LOGGER.debug("mapping the path " + path + " to path {}", mappedPath);
        if (mappedPath != null && (mappedPath.contains(Constants.HTTP_PROTOCOL) || mappedPath.contains(Constants.HTTPS_PROTOCOL))) {
            URI uri = new URI(mappedPath);
            return uri.getPath().trim();
        } else {
            return path;
        }
    }

}
