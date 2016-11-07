
/**
 * Modification on top of Adaptive Image component
 *
 * @author Sam
 *
 */
package au.com.auspost.global.core.adaptiveimage;
import au.com.auspost.global.utils.Constants;
import com.day.cq.commons.ImageHelper;
import com.day.cq.dam.api.Asset;
import com.day.cq.wcm.commons.AbstractImageServlet;
import com.day.cq.wcm.foundation.AdaptiveImageHelper;
import com.day.image.Layer;
import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.*;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.OptingServlet;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 * Servlet to render adaptive image component images in a variety of widths and qualities.
 */
@Properties({ @Property(name = "sling.servlet.resourceTypes", value = "sling/servlet/default"), //
	@Property(name = "sling.servlet.selectors", value = Constants.AUSPOST_IMAGE_SELECTOR, propertyPrivate = true),
    @Property(name = "sling.servlet.extensions", value = { "jpg", "png", "gif","jpeg" }), //
    @Property(name = "sling.servlet.methods", value = "GET") })
@Component(metatype = true, label = "Auspost Adaptive Image")
@Service
public class AdaptiveImageComponentServlet extends AbstractImageServlet implements OptingServlet{

	private final int CONST_WIDTH=3200;

    private static final Logger log = LoggerFactory.getLogger(AdaptiveImageComponentServlet.class);
    private static final long serialVersionUID = 42L;
    // Selector to indicate that we should not scale the image at all, only adjust the quality.
    private static final String FULL_SIZE_SELECTOR = "full";

    @Property(value = {
    		"43",
    		"50",
    		"60",
    		"63",
    		"70",
    		"83",
    		"85",
    		"86",
    		"88",
    		"100",
    		"102",
    		"116",
    		"119",
    		"120",
    		"125",
    		"126",
    		"140",
    		"150",
    		"163",
    		"165",
    		"166",
    		"170",
    		"175",
    		"176",
    		"195",
    		"198",
    		"200",
    		"204",
    		"210",
    		"228",
    		"231",
    		"232",
    		"238",
            "240",
            "245",
            "250",
            "300",
            "325",
            "326",
            "330",
            "340",
            "350",
            "390",
            "396",
            "400",
            "408",
            "420",
            "450",
            "455",
            "456",
            "462",
            "473",
            "480",
            "490",
            "485",
            "500",
            "502",
            "503",
            "585",
            "600",
            "635",
            "650",
            "660",
            "680",
            "650",
            "700",
            "765",
            "779",
            "780",
            "792",
            "840",
            "900",
            "910",
            "946",
            "960",
            "970",
            "1000",
            "1100",
            "1170",
            "1200",
            "1270",
            "1300",
            "1360",
            "1400",
            "1530",
            "1558",
            "1560",
            "1600",
            "1892",
            "1940",
            "2000",
            "2200",
            "2340",
            "2400",
            "2800",
            "3200",
            "3900",
            "4000"

    },
            label = "Supported Widths",
            description = "List of widths this component is permitted to generate.",unbounded=PropertyUnbounded.ARRAY)
    private static final String PROPERTY_SUPPORTED_WIDTHS = "adapt.supported.widths";
    private List<String> supportedWidths;

    @Override
	public boolean accepts(SlingHttpServletRequest request) {
    	/* 1. Resource must exist */
        if(request.getResource() instanceof org.apache.sling.api.resource.NonExistingResource) {
            return false;
        }

        /* 2. Resource must be an image. */
        final Asset asset =  request.getResource().adaptTo(Asset.class);
        final String contentType = asset != null ? asset.getMimeType() : request.getResource().getResourceMetadata().getContentType();
        if(!StringUtils.trimToEmpty(contentType).startsWith(Constants.IMAGE_SELECTOR)) {
            return false;
        }

        final List<String> selectors = Arrays.asList(request.getRequestPathInfo().getSelectors());
        boolean isAuspostImg = false;
        for(String str : selectors) {
        	if(str.equals(Constants.AUSPOST_IMAGE_SELECTOR)){
        		isAuspostImg = true;
        		break;
        	}
        }
        return isAuspostImg;
	}


    @Activate
    protected void activate(final Map<String, String> properties) {
        supportedWidths = new ArrayList<String>();
        String[] supportedWidthsArray = PropertiesUtil.toStringArray(properties.get(PROPERTY_SUPPORTED_WIDTHS));
        if (supportedWidthsArray != null && supportedWidthsArray.length > 0) {
            for (String width : supportedWidthsArray) {
                supportedWidths.add(width);
            }
        }
    }

    @Override
    protected Layer createLayer(ImageContext imageContext) throws RepositoryException, IOException {
    	SlingHttpServletRequest request = imageContext.request;
        String selectors[] = request.getRequestPathInfo().getSelectors();
        // We expect exactly 3 selectors OR only 1
        if (selectors.length != 3 && selectors.length != 1) {
            log.error("Unsupported number of selectors.");
            return null;
        }

        // selectors: [1] width, [2] quality
        int widthSelector = CONST_WIDTH;
        if (selectors.length == 3) {
            widthSelector = Integer.parseInt(selectors[1]);
        }
        // Ensure this is one of our supported widths
        if (isDimensionSupported(String.valueOf(widthSelector)) == false) {
            log.error("Unsupported width requested: {}.", widthSelector);
            return null;
        }

        final Asset asset = imageContext.resource.adaptTo(Asset.class);
        Layer layer;
        if(asset != null) {
            if(asset.getOriginal() == null) {
                throw new RuntimeException("Image asset has no original rendition.");
            }
            layer = ImageHelper.createLayer(imageContext.node.getSession(), asset.getOriginal().getPath());
        } else {
            layer = ImageHelper.createLayer(imageContext.resource);
        }
        Dimension dimension = new Dimension(widthSelector, 0);

        layer = ImageHelper.resize(layer,dimension,null,null);
        return layer;
    }

    /**
     * Query if this servlet has been configured to render images of the given width.
     * This method could be overridden to always return true in the case where any dimension
     * combination is permitted.
     * @param widthStr     Width of the image to render, or "full"
     * @return          true if the given dimensions are supported, false otherwise
     */
    protected boolean isDimensionSupported(String widthStr) {
        Iterator<String> iterator = getSupportedWidthsIterator();
        if (FULL_SIZE_SELECTOR.equals(widthStr)) {
            return true;
        }
        int width = Integer.parseInt(widthStr);
        while (iterator.hasNext()) {
            if (width == (Integer.parseInt(iterator.next()))) {
                return true;
            }
        }

        return false;
    }

    /**
     * An iterator to the collection of widths this servlet is configured to render.
     * @return  Iterator
     */
    protected Iterator<String> getSupportedWidthsIterator() {
        return supportedWidths.iterator();
    }

    @Override
    protected void writeLayer(SlingHttpServletRequest request, SlingHttpServletResponse response, ImageContext context, Layer layer) throws IOException, RepositoryException {
        double quality;
        // If the quality selector exists, use it
        String selectors[] = request.getRequestPathInfo().getSelectors();
        if (selectors.length == 3) {
            String imageQualitySelector = selectors[2];
            quality = getRequestedImageQuality(imageQualitySelector);
        } else {
            // If the quality selector does not exist, fall back to the default
            quality = getImageQuality();
        }

        writeLayer(request, response, context, layer, quality);
    }

    private double getRequestedImageQuality(String imageQualitySelector) {
        // If imageQualitySelector is not a valid Quality, fall back to teh default
        AdaptiveImageHelper.Quality newQuality = AdaptiveImageHelper.getQualityFromString(imageQualitySelector);
        if (newQuality != null ) {
            return newQuality.getQualityValue();
        }
        // Fall back to the defaut
        return getImageQuality();
    }

    @Override
    protected String getImageType() {
        return Constants.IMAGE_JPEG_NAME;
    }


}
