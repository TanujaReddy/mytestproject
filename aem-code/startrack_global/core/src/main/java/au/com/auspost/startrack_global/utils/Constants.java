package au.com.auspost.startrack_global.utils;

/**
 * Constants class to keep String constants
 */
public class Constants {


	private Constants(){
		//Don't allow instantiation.
	}

	//special characters
	public static final String FORWARD_SLASH = "/";
	public static final String HYPHEN = "-";
	public static final String EMPTY = "";
	public static final String SPACE = " ";
	public static final String UNDERSCORE = "_";
	public static final String COMMA = ",";
	public static final String STRING_CONTENT = "/content/";
	public static final String STRING_JCR_CONTENT = "/jcr:content";
	public static final String JCR_CONTENT = "jcr:content";

	//query params
	public static final String CURRENT_NODE_PATH = "currentNodePath";
	public static final String PARAM_NODE_TYPE = "paramNodeType";
	public static final String NODE_TO_BE_CREATED = "nodeToBeCreated";
	public static final String IMAGE_PATH = "imagePath";
	public static final String IMAGE_ALT_TEXT = "imageAltText";
	public static final String CHILD_NODE = "childNode";
	public static final String CHILD_NODE_PROPERTY = "childNodeProperty";
	public static final String PARAM_URL = "paramURL";
	public static final String HTTP ="http";
	public static final String HTTPS ="https";
	public static final String WWW ="www";
	public static final String DOT_HTML =".html";
	public static final String HTTP_URL  ="http://";

	//numbers
	public static final int ZERO = 0;
	public static final int ONE = 1;

	//property params
	public static final String PROPERTY_SLING_RESOURCE_TYPE = "sling:resourceType";
	public static final String PROPERTY_NAME = "name";
	public static final String PROPERTY_DESCRTIPTION = "description";
	public static final String PROPERTY_DESCRTIPTION_SAMPLE_TEXT = "This node has been dynamically created.";

	public static final String CONTAINER_SIZE = "containerSize";
	public static final String LAYOUT_CONTAINER_RESOURCETYPE = "startrack_global/components/lyt-container";
	public static final String LAYOUT_VARIATION = "layoutvariation";
	public static final String IMAGE_PATH_REF = "imagePathRef";

	public static final String COMMON_RENDITION_PATH = "/jcr:content/renditions/cq5dam.thumbnail.";
	public static final String SVG_RENDITION_PATH = "/jcr:content/renditions/original/jcr:content";

	public static final String TEXT = "text";

	public static final String ORIGINAL_RENDITION_PATH = "/jcr:content/renditions/original";

	public static final String NT_UNSTRUCTURED = "nt:unstructured";

	public static final String JCR_DATA = "jcr:data";

	public static final String FILE = "file";

	public static final String ALT_TXT_PROP = "altTextProp";

	public static final String IMAGE_PROP_NAME = "imagePropName";

	public static final String ADDITIONAL_PROP = "additionalProp";

	public static final String ALT_TEXT_PROP = "altTextProp";
	
    public static final String STARTRACK_IMAGE_SELECTOR = "startrackimage";
	
	public static final String IMAGE_SELECTOR = "image/";
	
	public static final String IMAGE_JPEG_NAME = "image/jpeg";
	
	public static final String CACHE_CONTROL_HEADER_KEY = "Cache-Control";

	//workflow related params
	public static final String USER_CATEGORY_AUTHOR = "author";
	public static final String USER_CATEGORY_APPROVER = "approver";
	public static final String OURPOST = "ourpost";
	public static final String GLOBAL = "startrack_global";


	public static final String PATH_CONTENT = "/content";
	public static final String PATH_ETC_TAGS = "/etc/tags";
	public static final String PATH_CONTENT_DAM = "/content/dam";
	public static final String USER_CATEGORY_ADMINISTRATORS = "administrators";
	public static final String ACTION_REQUESTED = "actionRequested";



}
