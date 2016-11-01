package au.com.auspost.startrack_global.core.util;

import org.apache.commons.lang3.StringUtils;

public class AusPostStringUtils {

	private AusPostStringUtils(){
		//Don't allow instantiation, methods should be called statically.
	}
	
	public static String removeHtmlAttributes(String str){
		if(StringUtils.isNotBlank(str)){
			str = str.replaceAll("\\<[^>]*>","");
		}
		return str;
	}
	
}
