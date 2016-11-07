package au.com.auspost.startrack_corp.core;

public class Constants {
    private Constants() {}

    //special characters
    public static final String FORWARD_SLASH = "/";
    public static final String HYPHEN = "-";
    public static final String EMPTY = "";
    public static final String SPACE = " ";
    public static final String UNDERSCORE = "_";
    public static final String COMMA = ",";

    //aem property params
    public static final String PROPERTY_SLING_RESOURCE_TYPE = "sling:resourceType";
    public static final String CONTENT_PARSYS = "contentParsys";
    public static final String SLING_RESOURCE_TYPE = "sling:resourceType";
    public static final String JCR_PRIMARY_TYPE = "jcr:primaryType";
    public static final String JCR_CONTENT = "jcr:content";
    public static final String JCR_TITLE = "jcr:title";
    public static final String JCR_DATA = "jcr:data";
    public static final String CQ_TAGS = "cq:tags";
    public static final String CQ_FAQ_TAGS = "cq:faqtags";
    public static final String CQ_HTG_TAGS = "cq:htgtags";
    public static final String CQ_PAGE = "cq:Page";
    public static final String LAYOUT = "layout";
    public static final String LAYOUT_CONTENT_PARSYS = "layoutcontainerParsys";

    public static final String CQ_TAG = "cq:Tag";
    public static final String CQ_TAG_TYPE = "cq/tagging/components/tag";

    //property params
    public static final String PROPERTY_NAME = "name";
    public static final String ALT_TEXT_PROP = "altTextProp";
    public static final String ADDITIONAL_PROPERTY = "additionalProp";
    public static final String IMAGE_PROP_NAME = "imagePropName";

    //path params
    public static final String TAGS_BASE_PATH = "/etc/tags/";

    //startrack specific properties
    public static final String STARTRACK_BASE_PATH = "/content/startrack_corp/startrack";
    public static final String STARTRACK_BASE_PATH2 = "/content/startrack_corp";
    public static final String STARTRACK_BASE_JCR_PATH = STARTRACK_BASE_PATH + FORWARD_SLASH + JCR_CONTENT;
    public static final String STARTRACK_SEARCH_JCR_PATH = STARTRACK_BASE_PATH + FORWARD_SLASH + JCR_CONTENT;
    public static final String JCR_CONTENT_PLUS_PARSYS = JCR_CONTENT + FORWARD_SLASH + CONTENT_PARSYS;
    public static final String JCR_CONTENT_TAGS_PARSYS = JCR_CONTENT + FORWARD_SLASH + CONTENT_PARSYS + LAYOUT + FORWARD_SLASH + LAYOUT_CONTENT_PARSYS;
    public static final String JCR_CONTENT_PLUS_TAGS = JCR_CONTENT + FORWARD_SLASH + CQ_TAGS;
    public static final String JCR_CONTENT_TAGS = JCR_CONTENT + FORWARD_SLASH + CQ_FAQ_TAGS;
    
    // TAGS
    
    public static final String TRACK_AND_TRACE = "cq:tracktracetags";
    public static final String SORRY_WE_MISSED_YOU = "cq:srywemisstags";
    public static final String PICK_UP_BOOKING = "cq:pickupbookingtags";
    public static final String TRANSIT_TIME = "cq:transittags";
    
}
