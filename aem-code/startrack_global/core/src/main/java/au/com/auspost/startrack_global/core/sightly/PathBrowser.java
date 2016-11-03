package au.com.auspost.startrack_global.core.sightly;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;

import com.adobe.cq.sightly.WCMUse;

import au.com.auspost.startrack_global.utils.Constants;

/**
 * This class is used to validate the external and internal links which gets genarated from the pathbrowser component of cta dialog.
 * eg : <sly data-sly-use.pathbrowser="${'au.com.auspost.startrack_global.core.sightly.PathBrowser' @ paramURL=item.ctaURL}"
 * data-sly-unwrap></sly>
 */
public class PathBrowser extends WCMUse {

	public String formattedURL = StringUtils.EMPTY;

	@Override
	public void activate() throws Exception {
		Resource resource = getResource();
		formattedURL = getFormattedURLString(resource);
	}

	public String getFormattedURLString(Resource resource) throws Exception {
		String paramURL = get(Constants.PARAM_URL, String.class);
		String ctaParams = get("ctaParams", String.class);
		if (StringUtils.isNotEmpty(paramURL)) {
			//Basically we are checking the link is internal link. Both the pages link and dam link start with /content/
			if (!(paramURL.startsWith(Constants.HTTP)) && !(paramURL.startsWith(Constants.HTTPS)) 
					&& paramURL.startsWith(Constants.STRING_CONTENT)) {
				//This is a internal link
				if ((paramURL.endsWith(Constants.FORWARD_SLASH))) {
					paramURL = StringUtils.removeEnd(paramURL, Constants.FORWARD_SLASH);
				}
				if(paramURL.endsWith(Constants.DOT_HTML)){
					paramURL = StringUtils.removeEnd(paramURL, Constants.DOT_HTML);
				}
				StringBuilder stringBuilder = new StringBuilder();
				stringBuilder.append(paramURL);
				if(StringUtils.isNotBlank(ctaParams) && ctaParams.equals(Constants.DOT_HTML)){
					stringBuilder.append(Constants.DOT_HTML);
				}else if(StringUtils.isNotBlank(ctaParams) && !ctaParams.contains(Constants.DOT_HTML)){
					stringBuilder.append(Constants.DOT_HTML.concat(ctaParams));
				}else if(StringUtils.isNotBlank(ctaParams) && ctaParams.contains(Constants.DOT_HTML)){
					stringBuilder.append(ctaParams);
				}
				
				//Finally check for .html and ctaParams
				if(!stringBuilder.toString().contains(Constants.DOT_HTML)){
					formattedURL = paramURL.concat(Constants.DOT_HTML).concat(StringUtils.isNotBlank(ctaParams)?ctaParams:"");
				}else{
					formattedURL = stringBuilder.toString();
				}
			}else{
				formattedURL = paramURL.concat(StringUtils.isNotBlank(ctaParams)?ctaParams:"");
			}
		}
		return formattedURL;
	}


	public String getFormattedURL() {
		return formattedURL;
	}
}