package au.com.auspost.global.core.sightly;

import org.apache.commons.lang3.StringUtils;

import com.adobe.cq.sightly.WCMUse;

import au.com.auspost.global.utils.Constants;

public class Accordion extends WCMUse {

	private String formattedNodePath;

	@Override
	public void activate() throws Exception {
		String currentNodePath = get(Constants.CURRENT_NODE_PATH, String.class);
		formattedNodePath = getFormattedNodePath(currentNodePath);
	}

	protected String getFormattedNodePath(String unformattedPathString) {
		String arr[] = new String []{Constants.STRING_CONTENT,Constants.STRING_JCR_CONTENT,
				Constants.UNDERSCORE,Constants.FORWARD_SLASH};
		for(String replace:arr){
			unformattedPathString = unformattedPathString.replaceAll(replace, StringUtils.EMPTY);
		}
		return unformattedPathString;
	}

	public String getFormattedNodePath() {
		return formattedNodePath;
	}
}