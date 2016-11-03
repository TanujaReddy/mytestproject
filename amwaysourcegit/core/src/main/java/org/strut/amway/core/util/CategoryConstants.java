package org.strut.amway.core.util;

public final class CategoryConstants {

    public static final String MAIN_BLOGGING_CATEGORY = "main-blogging";
    public static final String CORPORATE_CATEGORY = "corporate";
    public static final String CATEGORY_TYPE = "categoryType";

    public static final String ABS_PARENT = "absParent";
    //eg: /content/asia-pac/australia-new-zealand/australia/amway-today/en_au/Social_n_Digital/Digital_Tools.html
    public static final int SITE_LEVEL = 3; // Querying at this level returns the sites for the country
    public static final int LANGUAGE_LEVEL = 4; // Querying at this level returns the languages of the site
    public static final int ABS_LEVEL = 5; // Querying at this level returns the categories
    public static final String REL_PARENT = "relParent";
    public static final int REL_LEVEL = 0;

    public static final int CATEGORY_LEVEL = 6;
    public static final int SUB_CATEGORY_LEVEL = 7;
    public static final int TOP_NAVIGATION_DEPTH = 1;

}
