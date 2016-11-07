package au.com.auspost.startrack_global.core.sightly;

import java.util.LinkedHashMap;
import java.util.Map;

import au.com.auspost.startrack_global.core.util.PropertyUtil;
import com.adobe.cq.sightly.WCMUse;

import au.com.auspost.startrack_global.utils.Constants;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

public class SightlyStringUtils extends WCMUse{

	private static final Logger log = LoggerFactory.getLogger(SightlyStringUtils.class);

	private String items;
	private String svgItem;
	
	@Override
	public void activate() throws Exception {
		items = get("items",String.class);
		
		String svgItemPath = get("svgPath",String.class);
		if(StringUtils.isNotBlank(svgItemPath)){
			svgItem = getSvg(svgItemPath);
		}
	}

    public String getSvgItem() {
        return svgItem;
    }

    public Map<String,String> getHyphenSeparatedItems(){
		Map<String,String> itemList= new LinkedHashMap<String,String>();
		if(StringUtils.isNotBlank(items)){
			String arr [] = items.split(Constants.HYPHEN);
			itemList.put(arr[0], arr[1]);
		}
		return itemList;
	}
	/*
	 * This gets SVG code from svg image file from the path provided
	 */
	public String getSvg(String svgItemPath) throws RepositoryException {
		String svg = StringUtils.EMPTY;
		String svgPath = svgItemPath + Constants.SVG_RENDITION_PATH;
		Session session = getResourceResolver().adaptTo(Session.class);
			if(session!= null && !StringUtils.isBlank(svgPath) && session.nodeExists(svgPath )) {
                Node node = session.getNode(svgPath);
				if(node != null) {
					svg = PropertyUtil.returnSingleBinaryPropertyValue(node, Constants.JCR_DATA);
				}
            }
		return svg;
	}

}
